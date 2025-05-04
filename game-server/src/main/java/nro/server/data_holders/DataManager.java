package nro.server.data_holders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DataManager {

    private static final Logger log = LoggerFactory.getLogger(DataManager.class);

    private DataManager() {
        long start = System.currentTimeMillis();

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
