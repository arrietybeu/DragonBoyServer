package nro.server;

import nro.commons.database.DatabaseFactory;
import nro.commons.network.NioServer;
import nro.commons.network.ServerCfg;
import nro.commons.services.CronService;
import nro.commons.utils.SystemInfo;
import nro.commons.utils.concurrent.UncaughtExceptionHandler;
import nro.server.config.Config;
import nro.server.config.main.GameServerConfig;
import nro.server.config.network.NetworkConfig;
import nro.server.manager.ServerManager;
import nro.server.network.nro.GameConnectionFactoryImpl;
import nro.server.system.LogServer;
import nro.utils.ThreadPoolManager;
import nro.utils.cron.ThreadPoolManagerRunnableRunner;
import nro.utils.test.network.TestConnectionFactoryImpl;

import java.util.TimeZone;

public class DragonBall {

    private static NioServer nioServer;

    public static void mainz(String[] args) {
        Config.load();
        DatabaseFactory.init();
        ServerManager.launch();
        SystemInfo.logAll();
        LogServer.DebugLogic("Launching Dragon Ball...");
    }

    public static void main(String[] args) {
        Config.load();
        nioServer = initNioServer();
//        initUtilityServicesAndConfig();
//        Runtime.getRuntime().addShutdownHook(ShutdownHook.getInstance());
    }

    private static NioServer initNioServer() {
        NioServer nioServer = new NioServer(1,
                new ServerCfg(NetworkConfig.CLIENT_SOCKET_ADDRESS, "Test game clients", new TestConnectionFactoryImpl()));
        nioServer.connect(ThreadPoolManager.getInstance());
        return nioServer;
    }

    public static void shutdownNioServer() {
        if (nioServer != null) {
            nioServer.shutdown();
            nioServer = null;
        }
    }

    private static NioServer initNioServerz() {
        NioServer nioServer = new NioServer(NetworkConfig.NIO_READ_WRITE_THREADS,
                new ServerCfg(NetworkConfig.CLIENT_SOCKET_ADDRESS, "Nro game clients", new GameConnectionFactoryImpl()));
        nioServer.connect(ThreadPoolManager.getInstance());
        return nioServer;
    }

    private static void initUtilityServicesAndConfig() {
        // Set default uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());

        Config.load();
        // Second should be database factory
        DatabaseFactory.init();
//        PlayerDAO.setAllPlayersOffline();  // set player online = false
//        if (CleaningConfig.CLEANING_ENABLE) // clean database
//            DatabaseCleaningService.deletePlayersOnInactiveAccounts();

        // Initialize thread pools
        ThreadPoolManager.getInstance();

        // Initialize cron service
        CronService.initSingleton(ThreadPoolManagerRunnableRunner.class, TimeZone.getTimeZone(GameServerConfig.TIME_ZONE_ID));
    }

}