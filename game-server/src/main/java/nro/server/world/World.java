package nro.server.world;

import nro.server.data_holders.data.MapData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arriety
 */
public class World {

    private static final Logger log = LoggerFactory.getLogger(World.class);

    private final Map<Short, WorldMap> worldMaps = new HashMap<>();

    public World() {
        MapData.getInstance().forEachParalllel(template -> {
            WorldMap wm = new WorldMap(template);
            synchronized (worldMaps) {
                worldMaps.put(template.getId(), wm);
            }
        });
        log.info("World: {} world maps created.", worldMaps.size());
    }

    public WorldMap getMap(int mapId) {
        return worldMaps.get((short) mapId);
    }

    public WorldMapInstance getAvailableInstance(int mapId, int ownerId, int... zoneID) {
        WorldMap map = getMap(mapId);
        if (map == null)
            throw new NullPointerException("Failed to create position (invalid mapId: " + mapId + ")");

        byte typeMap = map.getTemplate().getTypeMap();

        switch (typeMap) {
            case 0 -> {
                return map.createInstanceForPlayer(ownerId);
            }
            case 1 -> {
                if (zoneID == null || zoneID.length == 0) {
                    return map.getRandomInstanceForOnline();
                }
                return map.getSharedZoneForOnline(zoneID[0]);
            }
            case 2 -> // Pho ban
            {
                return map.createInstanceForGuild(ownerId);
            }
            default -> throw new IllegalArgumentException("Unknown typeMap: " + typeMap);
        }
    }

    public WorldPosition createPosition(int mapId, int playerId, short x, short y, int... zoneID) {
        WorldMapInstance mr = this.getAvailableInstance(mapId, playerId, zoneID);
        if (mr == null) {
            log.info("Failed to create position (invalid coords: x={}, y={} for mapId {} in instanceId {})"
                    , x, y, mapId, zoneID);
            // đưa về nhà map base của các gender
            return null;
        }
        return new WorldPosition(mr.getParent().getTemplate().getId(), x, y, mr.getInstanceId(), mr);
    }

    public static World getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        protected static final World instance = new World();
    }
}
