package nro.server.engine.world;

import com.artemis.BaseSystem;
import nro.server.consts.ConstMap;
import nro.server.model.world.World;
import nro.server.model.world.WorldMap;
import nro.server.model.world.WorldMapInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class WorldGarbageCollectionSystem extends BaseSystem {

    private static final Logger log = LoggerFactory.getLogger(WorldGarbageCollectionSystem.class);

    private static final long GC_INTERVAL_MS = 10_000; // 10 giây

    private long lastRunTime = 0;

    @Override
    protected void processSystem() {
        long now = System.currentTimeMillis();
        if (now - lastRunTime < GC_INTERVAL_MS) return;
        lastRunTime = now;

        for (WorldMap map : World.getInstance().getWorldMaps().values()) {
            byte type = map.getTemplate().getTypeMap();
            if (type == ConstMap.MAP_TYPE_NORMAL) continue;

            Iterator<WorldMapInstance> it = map.getAllAreas().iterator();

            while (it.hasNext()) {
                WorldMapInstance inst = it.next();

                if (inst == null) continue;
                if (inst.getPlayerCount() > 0) {
                    inst.setScheduledForRemoval(false);
                    continue;
                }

                if (!inst.isScheduledForRemoval()) {
                    inst.setScheduledForRemoval(true);
                    inst.setLastEmptyTime(now);
                    continue;
                }

                long elapsed = now - inst.getLastEmptyTime();
                if (elapsed >= ConstMap.TIMEOUT_MILLIS) {
                    log.info("GC WorldMapInstance: map={}, instanceId={}, removed entities={}",
                            map.getId(), inst.getInstanceId(), inst.getEntities().size());

                    inst.getEntities().forEach(e -> {
                        if (e != null && e.isActive()) {
                            e.deleteFromWorld();
                        }
                    });

                    it.remove();
                }
            }
        }
    }
}
