package nro.server.model.world;

import lombok.Getter;
import nro.server.consts.ConstMap;
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

    @Getter
    private final Map<Short, WorldMap> worldMaps = new HashMap<>();

    public World() {
        MapData.getInstance().forEachParalllel(template -> {
            WorldMap wm = new WorldMap(template);
            synchronized (worldMaps) {
                worldMaps.put(wm.getTemplate().getId(), wm);
            }
        });
        log.info("World: {} world maps created.", worldMaps.size());
    }

    public WorldMap getMap(int mapId) {
        return worldMaps.get((short) mapId);
    }

    public WorldMapInstance getAreaInMap(int mapId, int areaId) {
        WorldMap map = getMap(mapId);
        if (map == null) {
            throw new NullPointerException("Invalid mapId: " + mapId + " when getting areaId: " + areaId);
        }
        return map.getWorldMapInstance(areaId);
    }

    public WorldMapInstance getAvailableInstance(int mapId, int ownerId, int... zoneID) {
        WorldMap map = getMap(mapId);
        if (map == null) {
            throw new NullPointerException("Invalid mapId: " + mapId);
        }

        byte typeMap = map.getTemplate().getTypeMap();

        return switch (typeMap) {
            case ConstMap.MAP_OFFLINE -> map.getOrCreateUniqueInstance(ownerId); // Offline: unique per player.
            case ConstMap.MAP_TYPE_NORMAL -> {
                if (zoneID != null && zoneID.length > 0) {
                    yield map.getSharedInstance(zoneID[0]);
                }
                yield map.getRandomSharedInstance();
            }
//            case ConstMap.MAP_PHO_BAN -> map.getOrCreateUniqueInstance(ownerId); // Phó bản: unique per guild.
            default -> throw new IllegalArgumentException("Unknown typeMap: " + typeMap);
        };
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

    public String logInfo() {
        StringBuilder sb = new StringBuilder();

        for (var map : getInstance().getWorldMaps().values()) {
            sb.append("Map ID: ").append(map.getTemplate().getId())
                    .append(", Name: ").append(map.getTemplate().getName())
                    .append(", Instances: ").append(map.getAreas().size())
                    .append("\n");
        }
        return sb.toString();
    }

    public static World getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        protected static final World instance = new World();
    }
}
