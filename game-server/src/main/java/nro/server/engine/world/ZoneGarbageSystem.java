package nro.server.engine.world;

import com.artemis.BaseSystem;
import nro.server.model.map.GameMap;
import nro.server.model.map.GameMapFactory;
import nro.server.model.map.zone.GC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arriety
 */
public class ZoneGarbageSystem extends BaseSystem {

    private static final Logger log = LoggerFactory.getLogger(ZoneGarbageSystem.class);

    private static final long GC_INTERVAL_MS = 10_000;
    private long lastRunTime = 0;

    @Override
    protected void processSystem() {
        long now = System.currentTimeMillis();
        if (now - lastRunTime < GC_INTERVAL_MS) return;
        lastRunTime = now;

        for (GameMap map : GameMapFactory.getInstance().getAllMaps()) {
            var manager = map.zoneManager();
            if (manager instanceof GC gcManager) {
                try {
                    gcManager.gc();
                } catch (Throwable t) {
                    log.warn("GC error on map {}: {}", map.id(), t.getMessage(), t);
                }
            }
        }
    }
}
