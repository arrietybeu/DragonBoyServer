package nro.server.data_holders;

import nro.server.data_holders.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class DataManager {

    private static final Logger log = LoggerFactory.getLogger(DataManager.class);

    private static final List<IManager> MANAGERS = List.of(
            VersionImageData.getInstance(),
            TileImageData.getInstance(),
            DartData.getInstance(),
            ArrowPaintData.getInstance(),
            EffectCharPaintData.getInstance(),
            PartData.getInstance(),
            SkillPaintData.getInstance(),
            ImageData.getInstance()
    );

    private DataManager() {
        long start = System.currentTimeMillis();
        for (IManager manager : MANAGERS) {
            try {
                manager.init();
                log.info("Initialized manager class: [{}]", manager.getClass().getSimpleName());
            } catch (Throwable e) {
                log.error("Error initializing manager: {} - {}", manager.getClass().getSimpleName(), e.getMessage(), e);
            }
        }
        long time = System.currentTimeMillis() - start;
        log.info("##### [Static Data loaded in {} seconds] #####", String.format("%.1f", time / 1000f));
    }

    public static DataManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        protected static final DataManager instance = new DataManager();
    }

}
