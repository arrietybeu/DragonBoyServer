package nro.server;

import nro.server.configs.Config;
import nro.server.utils.LogServer;
import nro.server.utils.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameServer.class);

    public static void main(String[] args) {
        initUtilityServicesAndConfig();
    }

    private static void initUtilityServicesAndConfig() {
        Config.load();

        ThreadPoolManager pool = ThreadPoolManager.getInstance();
        pool.getStats().forEach(line -> LOGGER.info(LogServer.ANSI_GREEN + "{}" + LogServer.ANSI_RESET, line));
    }

}
