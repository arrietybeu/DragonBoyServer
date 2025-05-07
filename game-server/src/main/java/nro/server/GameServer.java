package nro.server;

import lombok.Getter;
import nro.commons.network.NioServer;
import nro.commons.network.ServerCfg;
import nro.commons.services.CronService;
import nro.commons.utils.SystemInfo;
import nro.commons.utils.concurrent.UncaughtExceptionHandler;
import nro.server.configs.Config;
import nro.server.configs.main.PacketConfig;
import nro.server.configs.network.NetworkConfig;
import nro.server.data_holders.DataManager;
import nro.server.network.nro.GameConnectionFactory;
import nro.server.network.nro.client_packets.NroClientPacketFactory;
import nro.server.network.nro.server_packets.ServerPacketsCommand;
import nro.server.services.CommandService;
import nro.server.utils.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimeZone;

public class GameServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameServer.class);
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    @Getter
    private static NioServer nioServer;

    public static void main(String[] args) {
        initUtilityServicesAndConfig();

        DataManager.getInstance();

        System.gc();

        nioServer = initNioServer();

        Runtime.getRuntime().addShutdownHook(ShutdownHook.getInstance());
    }

    private static NioServer initNioServer() {
        ServerCfg serverCfg = new ServerCfg(NetworkConfig.CLIENT_SOCKET_ADDRESS, "Nro game clients", new GameConnectionFactory());
        NioServer nioServer = new NioServer(NetworkConfig.NIO_READ_WRITE_THREADS, serverCfg);
        nioServer.connect(ThreadPoolManager.getInstance());
        return nioServer;
    }

    private static void initUtilityServicesAndConfig() {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
        Config.load();
        ServerPacketsCommand.init(PacketConfig.SERVER_PACKET_COMMAND);
        NroClientPacketFactory.init(PacketConfig.CLIENT_PACKET_COMMAND);
        // Initialize thread pools
        ThreadPoolManager pool = ThreadPoolManager.getInstance();
        pool.getStats().forEach(line -> LOGGER.info(ANSI_GREEN + "{}" + ANSI_RESET, line));
        SystemInfo.logAll();
        // Initialize scanner
        initCommandService();
//        CronService.initSingleton(ThreadPoolManagerRunnableRunner.class, TimeZone.getTimeZone(GSConfig.TIME_ZONE_ID));
    }

    private static void initCommandService() {
        new Thread(CommandService::ActiveCommandLine, "CommandLine Thread").start();
    }

    public static void shutdownNioServer() {
        if (nioServer != null) {
            nioServer.shutdown();
            nioServer = null;
        }
    }

    public static boolean isShuttingDownSoon() {
        return ShutdownHook.getInstance().isRunning() && ShutdownHook.getInstance().getRemainingSeconds() <= 30;
    }

}
