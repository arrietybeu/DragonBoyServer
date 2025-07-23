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
    private final Map<Integer, Integer> ownerToInstance = new ConcurrentHashMap<>();

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

        if (template.getTypeMap() == 1) {
            for (int i = 0; i < template.getMaxArea(); i++) {
                areas.add(i, new WorldMapInstance(this, i));
            }
        } else {
            areas.addFirst(new WorldMapInstance(this, 0));
        }
    }

    public WorldMapInstance getWorldMapInstance(int instanceId) {
        return areas.get(instanceId);
    }

    public WorldMapInstance createInstanceForPlayer(int playerId) {
        return createUniqueInstance(playerId);
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
        int nextId = generateNextInstanceId();
        WorldMapInstance instance = new WorldMapInstance(this, nextId, ownerId);
        areas.add(nextId, instance);
        if (ownerId != 0) ownerToInstance.put(ownerId, nextId);
        return instance;
    }

    private int generateNextInstanceId() {
        int id = 1;
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
        int nextId = generateNextInstanceId();
        WorldMapInstance instance = new WorldMapInstance(this, nextId);
        areas.add(nextId, instance);
        return instance;
    }

    public static WorldMapInstance getArea(int mapId, int areaId, int entityId) {
        WorldMap map = World.getInstance().getMap(mapId);
        if (map == null) {
            throw new IllegalArgumentException("Invalid mapId: " + mapId);
        }

        var ecsWorld = GameWorld.getInstance().getWorld();
        var entity = ecsWorld.getEntity(entityId);

        if (entity == null) {
            throw new IllegalArgumentException("Entity with ID " + entityId + " not found.");
        }

        byte typeMap = map.getTemplate().getTypeMap();

        if (entity.getComponent(PlayerComponent.class) != null) {
            return switch (typeMap) {
                case 0 -> map.createInstanceForPlayer(entityId); // offline
                case 1 -> map.getSharedZoneForOnline(areaId);          // online
                // case 2 -> {
                //     ClanComponent clan = entity.getComponent(ClanComponent.class);
                //     int guildId = (clan != null) ? clan.id : -1;
                //     yield map.createInstanceForGuild(guildId); // phó bản
                // }
                default -> throw new IllegalStateException("Unknown typeMap for Player: " + typeMap);
            };
        }

        if (entity.getComponent(BossComponent.class) != null) {
            return (areaId >= 0)
                    ? map.getWorldMapInstance(areaId)
                    : map.getWorldMapInstance(0);
        }

        throw new UnsupportedOperationException("Unsupported entity type: " + entity.getClass().getSimpleName());
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
