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

    private final Map<Integer, WorldMap> worldMaps = new HashMap<>();

    public World() {
        MapData.getInstance().forEachParalllel(template -> {
            WorldMap wm = new WorldMap(template);
            synchronized (worldMaps) {
                worldMaps.put(template.getId(), wm);
            }
        });
        log.info("World: {} world maps created.", worldMaps.size());
    }

    public void registerMap(WorldMap map) {
        worldMaps.put(map.getId(), map);
    }

    public WorldMap getMap(int mapId) {
        return worldMaps.get(mapId);
    }

    public static World getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        protected static final World instance = new World();
    }
}
