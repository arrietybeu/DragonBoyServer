package nro.server.model.world;

import lombok.Getter;
import nro.server.consts.ConstMap;
import nro.server.data_holders.data.MapData;
import nro.server.engine.entity.GameWorld;
import nro.server.model.ecs.component.boss.BossComponent;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.model.templates.world.TileMap;
import nro.server.model.templates.world.Waypoint;
import nro.server.model.templates.world.WorldMapTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Arriety
 */
@Getter
public class WorldMap {

    private static final Logger log = LoggerFactory.getLogger(WorldMap.class);

    private final int id;
    private final String name;
    private final WorldMapTemplate template;

    /**
     * chỉ thao tác qua addArea/removeArea/getArea
     */
    private final List<WorldMapInstance> areas = new ArrayList<>();
    private final Map<Integer, Byte> ownerToInstance = new ConcurrentHashMap<>();

    private final ReentrantReadWriteLock areasLock = new ReentrantReadWriteLock();

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
                addArea(new WorldMapInstance(this, i));
            }
//        } else {
//            addArea(new WorldMapInstance(this, (byte) 0));
        }
    }

    /**
     * Thread‑safe read
     **/
    public WorldMapInstance getArea(byte instanceId) {
        areasLock.readLock().lock();
        try {
            if (instanceId < 0 || instanceId >= areas.size()) {
                return null;
            }
            return areas.get(instanceId);
        } finally {
            areasLock.readLock().unlock();
        }
    }

    /**
     * Dùng getAllAreasSafe() cho mọi thao tác chỉ đọc, an toàn và đơn giản.
     **/
    public List<WorldMapInstance> getAllAreasSafe() {
        areasLock.readLock().lock();
        try {
            return new ArrayList<>(areas);
        } finally {
            areasLock.readLock().unlock();
        }
    }

    /**
     * Dùng khi cần đọc, chỉnh sửa
     *
     * @return
     */
    public List<WorldMapInstance> getAllAreas() {
        areasLock.readLock().lock();
        try {
            return areas;
        } finally {
            areasLock.readLock().unlock();
        }
    }

    /**
     * Thread‑safe write
     **/
    public void addArea(WorldMapInstance instance) {
        areasLock.writeLock().lock();
        try {
            int idx = instance.getInstanceId();
            while (areas.size() <= idx) {
                areas.add(null);
            }
            areas.set(idx, instance);
            if (instance.getOwnerId() != 0) {
                ownerToInstance.put(instance.getOwnerId(), (byte) idx);
            }
        } finally {
            areasLock.writeLock().unlock();
        }
    }

    /**
     * Thread‑safe write
     **/
    public WorldMapInstance removeArea(byte instanceId) {
        areasLock.writeLock().lock();
        try {
            if (instanceId < 0 || instanceId >= areas.size()) {
                return null;
            }
            WorldMapInstance removed = areas.get(instanceId);
            if (removed != null) {
                ownerToInstance.values().removeIf(b -> b == instanceId);
                areas.set(instanceId, null);
            }
            return removed;
        } finally {
            areasLock.writeLock().unlock();
        }
    }

    public WorldMapInstance getWorldMapInstance(int instanceId) {
        return getArea((byte) instanceId);
    }

    public WorldMapInstance createInstanceForPlayer(int playerId) {
        return getOrCreateUniqueInstance(playerId);
    }

    /**
     * synchronized để đảm bảo ownerToInstance nhất quán
     */
    public synchronized WorldMapInstance getOrCreateUniqueInstance(int ownerId) {
        if (ownerId < 0) {
            throw new IllegalArgumentException("Invalid ownerId: " + ownerId);
        }

        Byte existing = ownerToInstance.get(ownerId);
        if (existing != null) {
            WorldMapInstance inst = getArea(existing);
            if (inst != null) {
                return inst;
            }
        }

        byte nextId = generateNextInstanceId();
        WorldMapInstance instance = new WorldMapInstance(this, nextId, ownerId);
        GameWorld.getInstance().getWorld().inject(instance);
        instance.initNpc();
        addArea(instance);
        ownerToInstance.put(ownerId, nextId);
        return instance;
    }

    public WorldMapInstance createInstanceForGuild(int guildId) {
        return getOrCreateUniqueInstance(guildId);
    }

    public WorldMapInstance getSharedInstance(int zoneId) {
        WorldMapInstance inst = getArea((byte) zoneId);
        if (inst != null && inst.getPlayerCount() < template.getMaxPlayer()) {
            return inst;
        }
        return null;
    }

    public WorldMapInstance getRandomSharedInstance() {
        for (WorldMapInstance inst : getAllAreasSafe()) {
            if (inst != null && inst.getPlayerCount() < template.getMaxPlayer()) {
                return inst;
            }
        }
        return null;
    }

    public WorldMapInstance getRandomInstanceForOnline() {
        List<WorldMapInstance> list = getAllAreasSafe();
        for (WorldMapInstance inst : list) {
            if (inst != null && inst.getPlayerCount() <= template.getMaxPlayer()) {
                return inst;
            }
        }
        return null;
    }

    public WorldMapInstance getSharedZoneForOnline(int zoneID) {
        return getSharedInstance(zoneID);
    }


    /**
     * Không thao tác trực tiếp vào areas, sử dụng helper
     **/
    private byte generateNextInstanceId() {
        areasLock.readLock().lock();
        try {
            byte id = 1;
            while (id < areas.size() && areas.get(id) != null) {
                id++;
            }
            return id;
        } finally {
            areasLock.readLock().unlock();
        }
    }

    public WorldMapTemplate getTemplate() {
        return template;
    }

    public WorldMapInstance getInstance(int instanceId) {
        return getArea((byte) instanceId);
    }

    public WorldMapInstance createNewInstance() {
        byte nextId = generateNextInstanceId();
        WorldMapInstance instance = new WorldMapInstance(this, nextId);
        addArea(instance);
        return instance;
    }

    public static WorldMapInstance getArea(int mapId, int areaId, int entityId, int playerId) {
        WorldMap map = World.getInstance().getMap(mapId);
        if (map == null) throw new IllegalArgumentException("Invalid mapId: " + mapId);

        var ecsWorld = GameWorld.getInstance().getWorld();
        var entity = ecsWorld.getEntity(entityId);
        if (entity == null) throw new IllegalArgumentException("Entity not found: " + entityId);

        byte typeMap = map.getTemplate().getTypeMap();
        if (entity.getComponent(PlayerComponent.class) != null) {
            return switch (typeMap) {
                case ConstMap.MAP_OFFLINE -> map.getOrCreateUniqueInstance(playerId);
                case ConstMap.MAP_TYPE_NORMAL -> (areaId >= 0)
                        ? map.getSharedInstance(areaId)
                        : map.getRandomSharedInstance();
                case ConstMap.MAP_PHO_BAN -> map.getOrCreateUniqueInstance(playerId);
                default -> throw new IllegalStateException("Unknown typeMap: " + typeMap);
            };
        }
        if (entity.getComponent(BossComponent.class) != null) {
            return (areaId >= 0) ? map.getInstance(areaId) : map.getInstance(0);
        }
        throw new UnsupportedOperationException("Unsupported entity for playerId: " + playerId);
    }

    public Waypoint getWayPointInMap(int x, int y, int playerID) {
        try {
            if (id == 46) {
                int delta = 1000;
                var sub = template.getWaypointMap().subMap(x - delta, true, x + delta, true);
                for (var list : sub.values()) {
                    for (Waypoint wp : list) {
                        if (x >= wp.getMinX() - delta && x <= wp.getMaxX() + delta
                                && y >= wp.getMinY() && y <= wp.getMaxY()) {
                            return wp;
                        }
                    }
                }
            } else {
                var entry = template.getWaypointMap().floorEntry(x);
                if (entry != null) {
                    for (Waypoint wp : entry.getValue()) {
                        if (x >= wp.getMinX() && x <= wp.getMaxX()
                                && y >= wp.getMinY() && y <= wp.getMaxY()) {
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
        if (tileId < 0 || tileId >= MapData.getInstance().tileIndex.length) return;

        int[][] indexList = MapData.getInstance().tileIndex[tileId];
        int[] typeList = MapData.getInstance().tileType[tileId];
        int[] tiles = tileMap.tiles();
        for (int i = 0; i < tiles.length; i++) {
            int t = tiles[i];
            for (int j = 0; j < indexList.length; j++) {
                for (int val : indexList[j]) {
                    if (t == val) {
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
        int idx = y * tileMap.width() + x;
        return (idx < 0 || idx >= types.length) ? 1000 : types[idx];
    }

    public short touchY(int px, int py) {
        int tx = px / SIZE, y = py, w = tileMap.width();
        if (tx < 0 || tx >= w) return (short) pixelHeight;
        while (y < pixelHeight) {
            int ty = y / SIZE;
            int idx = ty * w + tx;
            if ((types[idx] & ConstMap.T_TOP) != 0) return (short) (ty * SIZE);
            y++;
        }
        return (short) pixelHeight;
    }

    public boolean isTouchY(int x, int y) {
        int tx = x / SIZE, ty = y / SIZE, w = tileMap.width(), h = tileMap.height();
        if (tx < 0 || tx >= w) return false;
        for (int j = ty; j < h; j++) {
            int idx = j * w + tx;
            if ((types[idx] & ConstMap.T_TOP) != 0) return true;
        }
        return false;
    }

    public boolean isPlayerOnGround(int x, int y) {
        return (tileTypeAtPixel(x, y + 1) & ConstMap.T_TOP) != 0;
    }
}
