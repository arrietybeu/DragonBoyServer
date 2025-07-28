package nro.server.world;

import lombok.Getter;
import nro.server.consts.ConstMap;
import nro.server.data_holders.data.MapData;
import nro.server.engine.GameWorld;
import nro.server.model.ecs.component.boss.BossComponent;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.model.templates.world.TileMap;
import nro.server.model.templates.world.Waypoint;
import nro.server.model.templates.world.WorldMapTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Arriety
 */
@Getter
public class WorldMap {

    private static final Logger log = LoggerFactory.getLogger(WorldMap.class);

    private final int id;
    private final String name;
    private final WorldMapTemplate template;

    private final List<WorldMapInstance> areas = new ArrayList<>();
    private final Map<Integer, Byte> ownerToInstance = new ConcurrentHashMap<>();

    private final int[] types;
    private final TileMap tileMap;
    private final int pixelWidth;
    private final int pixelHeight;
    private static final int SIZE = 24;

    public WorldMap(WorldMapTemplate template) {
        this.id = template.getId();
        this.name = template.getName();
        this.template = template;
        this.tileMap = template.getTileMap();
        this.pixelHeight = tileMap.height() * SIZE;
        this.pixelWidth = tileMap.width() * SIZE;
        this.types = new int[tileMap.tiles().length];
        loadTileTypes();

        if (template.getTypeMap() == ConstMap.MAP_TYPE_NORMAL) {
            for (byte i = 0; i < template.getMaxArea(); i++) {
                areas.add(i, new WorldMapInstance(this, i));
            }
        } else {
            areas.addFirst(new WorldMapInstance(this, (byte) 0));
        }
    }

    public WorldMapInstance getWorldMapInstance(int instanceId) {
        return areas.get(instanceId);
    }

    public WorldMapInstance createInstanceForPlayer(int playerId) {
        return createUniqueInstance(playerId);
    }

    public synchronized WorldMapInstance getOrCreateUniqueInstance(int ownerId) {
        if (ownerId < 0) {
            throw new IllegalArgumentException("Invalid ownerId: " + ownerId);
        }

        Byte instanceId = ownerToInstance.get(ownerId);
        if (instanceId != null) {
            WorldMapInstance instance = areas.get(instanceId);
            if (instance != null) {
                return instance;
            }
        }

        byte nextId = generateNextInstanceId();
        WorldMapInstance instance = new WorldMapInstance(this, nextId, ownerId);
        if (nextId < areas.size()) {
            areas.set(nextId, instance);
        } else {
            areas.add(instance);
        }
        ownerToInstance.put(ownerId, nextId);
        return instance;
    }

    public WorldMapInstance getSharedInstance(int zoneId) {
        if (zoneId < 0 || zoneId >= areas.size()) {
            log.warn("Invalid zoneId: {} for map: {}", zoneId, id);
            return null;
        }
        WorldMapInstance instance = areas.get(zoneId);
        if (instance != null && instance.getPlayerCount() < template.getMaxPlayer()) {
            return instance;
        }
        return null;
    }

    public WorldMapInstance getRandomSharedInstance() {
        for (WorldMapInstance instance : areas) {
            if (instance != null && instance.getPlayerCount() < template.getMaxPlayer()) {
                return instance;
            }
        }
        return null;
    }

    public WorldMapInstance getSharedZoneForOnline(int zoneID) {
        if (zoneID < 0 || zoneID >= template.getMaxArea()) {
            log.warn("Invalid zoneID: {} for map: {}", zoneID, id);
            return null;
        }
        WorldMapInstance inst = areas.get(zoneID);
        if (inst != null && inst.getPlayerCount() < template.getMaxPlayer()) {
            return inst;
        }
        return null;
    }

    public WorldMapInstance getRandomInstanceForOnline() {

        for (int i = 0; i < areas.size(); i++) {

            WorldMapInstance inst = areas.get(i);
            if (inst == null) {
                throw new RuntimeException("zone id: " + i + " is null");
            }
            if (inst.getPlayerCount() > template.getMaxPlayer()) {
                continue;
            }

            return inst;
        }
        return null;
    }

    /**
     * Dùng cho bản đồ dạng phó bản (typeMap = 2), nơi mỗi bang hội sẽ có một bản sao riêng của bản đồ (area riêng biệt).
     *
     * @param guildId
     * @return
     */
    public WorldMapInstance createInstanceForGuild(int guildId) {
        return ownerToInstance.containsKey(guildId) ? areas.get(ownerToInstance.get(guildId))
                : createUniqueInstance(guildId);
    }

    private synchronized WorldMapInstance createUniqueInstance(int ownerId) {
        if (ownerId < 0) {
            throw new IllegalArgumentException("Owner ID must be non-negative, got: " + ownerId);
        }

        if (ownerToInstance.containsKey(ownerId)) {
            int existingInstanceId = ownerToInstance.get(ownerId);
            WorldMapInstance existingInstance = areas.get(existingInstanceId);
            if (existingInstance != null) {
                return existingInstance;
            }
        }

        byte nextId = generateNextInstanceId();
        WorldMapInstance instance = new WorldMapInstance(this, nextId, ownerId);

        areas.add(nextId, instance);
        if (ownerId != 0) ownerToInstance.put(ownerId, nextId);
        return instance;
    }

    private byte generateNextInstanceId() {
        byte id = 1;
//        while (area.containsKey(id)) id++;
        while (id < areas.size() && areas.get(id) != null) id++;
        return id;
    }

    public WorldMapTemplate getTemplate() {
        return template;
    }

    public WorldMapInstance getInstance(int instanceId) {
        if (instanceId < 0 || instanceId >= areas.size()) {
            log.warn("Invalid instanceId: {} for map: {}", instanceId, id);
            return null;
        }
        return areas.get(instanceId);
    }

    public Collection<WorldMapInstance> getAllArea() {
        return areas;
    }

    public WorldMapInstance createNewInstance() {
        byte nextId = generateNextInstanceId();
        WorldMapInstance instance = new WorldMapInstance(this, nextId);
        areas.add(nextId, instance);
        return instance;
    }

    public static WorldMapInstance getArea(int mapId, int areaId, int entityId, int playerId) {
        WorldMap map = World.getInstance().getMap(mapId);
        if (map == null) {
            throw new IllegalArgumentException("Invalid mapId: " + mapId);
        }

        var ecsWorld = GameWorld.getInstance().getWorld();
        var entity = ecsWorld.getEntity(entityId);
        if (entity == null) {
            throw new IllegalArgumentException("Entity not found: " + entityId);
        }

        byte typeMap = map.getTemplate().getTypeMap();

        if (entity.getComponent(PlayerComponent.class) != null) {
            return switch (typeMap) {
                case ConstMap.MAP_OFFLINE -> map.getOrCreateUniqueInstance(playerId); // Offline: unique per player.
                case ConstMap.MAP_TYPE_NORMAL ->
                        (areaId >= 0) ? map.getSharedInstance(areaId) : map.getRandomSharedInstance(); // Online: shared.
//                case ConstMap.MAP_PHO_BAN ->
//                        map.getOrCreateUniqueInstance(playerId); // Phó bản: unique (giả sử guildId = playerId hoặc adjust).
                default -> throw new IllegalStateException("Unknown typeMap: " + typeMap);
            };
        }

        if (entity.getComponent(BossComponent.class) != null) {
            return (areaId >= 0) ? map.getInstance(areaId) : map.getInstance(0);
        }

        throw new UnsupportedOperationException("Unsupported entity: " + entity.getComponent(PlayerComponent.class).connection.getPlayerID());
    }

    public Waypoint getWayPointInMap(int x, int y, int playerID) {
        try {
            if (this.id == 46) {
                int deltaX = 1000;
                NavigableMap<Integer, List<Waypoint>> subMap = template.getWaypointMap().subMap(x - deltaX, true,
                        x + deltaX, true);
                for (List<Waypoint> waypoints : subMap.values()) {
                    for (Waypoint wp : waypoints) {
                        if (x >= wp.getMinX() - deltaX && x <= wp.getMaxX() + deltaX &&
                                y >= wp.getMinY() && y <= wp.getMaxY()) {
                            return wp;
                        }
                    }
                }
            } else {
                Map.Entry<Integer, List<Waypoint>> entry = template.getWaypointMap().floorEntry(x);
                if (entry != null) {
                    for (Waypoint wp : entry.getValue()) {
                        if (x >= wp.getMinX() && x <= wp.getMaxX() && y >= wp.getMinY() && y <= wp.getMaxY()) {
                            return wp;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error getting waypoint for player: " + playerID, ex);
        }
        return null;
    }

    private void loadTileTypes() {
        int tileId = template.getTileId() - 1;

        if (tileId < 0 || tileId >= MapData.getInstance().tileIndex.length) {
//            log.warn("Map {} name {} has invalid tileId: {}", id, name, tileId);
            return;
        }

        int[][] indexList = MapData.getInstance().tileIndex[tileId];
        int[] typeList = MapData.getInstance().tileType[tileId];
        int[] tiles = tileMap.tiles();

        for (int i = 0; i < tiles.length; i++) {
            int tile = tiles[i];
            for (int j = 0; j < indexList.length; j++) {
                for (int indexVal : indexList[j]) {
                    if (tile == indexVal) {
                        types[i] |= typeList[j];
                        break;
                    }
                }
            }
        }
    }

    public int tileTypeAtPixel(int px, int py) {
        int x = px / SIZE, y = py / SIZE;
        if (x < 0 || y < 0 || x >= tileMap.width() || y >= tileMap.height()) return 1000;
        int index = y * tileMap.width() + x;
        return (index < 0 || index >= types.length) ? 1000 : types[index];
    }

    public short touchY(int px, int py) {
        int tx = px / SIZE;
        int y = py;
        int width = tileMap.width();

        if (tx < 0 || tx >= width) return (short) pixelHeight;

        while (y < pixelHeight) {
            int ty = y / SIZE;
            int index = ty * width + tx;
            if ((types[index] & ConstMap.T_TOP) != 0) {
                return (short) (ty * SIZE);
            }
            y++;
        }
        return (short) pixelHeight;
    }

    public boolean isTouchY(int x, int y) {
        int tx = x / SIZE;
        int ty = y / SIZE;
        int width = tileMap.width();
        int height = tileMap.height();

        if (tx < 0 || tx >= width) return false;

        for (int j = ty; j < height; j++) {
            int index = j * width + tx;
            if ((types[index] & ConstMap.T_TOP) != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isPlayerOnGround(int x, int y) {
        return (tileTypeAtPixel(x, y + 1) & ConstMap.T_TOP) != 0;
    }

}
