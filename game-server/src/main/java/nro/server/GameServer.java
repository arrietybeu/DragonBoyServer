package nro.server;

import com.artemis.WorldConfigurationBuilder;
import lombok.Getter;
import nro.commons.database.DatabaseFactory;
import nro.commons.network.NioServer;
import nro.commons.network.ServerCfg;
import nro.commons.services.CronService;
import nro.commons.utils.concurrent.UncaughtExceptionHandler;
import nro.server.configs.Config;
import nro.server.configs.main.ConfigServer;
import nro.server.configs.main.PacketConfig;
import nro.server.configs.network.NetworkConfig;
import nro.server.controllers.BannedIpController;
import nro.server.data_holders.DataManager;
import nro.server.engine.GameWorld;
import nro.server.network.nro.GameConnectionFactory;
import nro.server.network.nro.client_packets.NroClientPacketFactory;
import nro.server.network.nro.server_packets.ServerPacketsCommand;
import nro.server.services.CommandService;
import nro.server.system.FashionUpdateSystem;
import nro.server.system.MovementSystem;
import nro.server.utils.ThreadPoolManager;
import nro.server.utils.ThreadPoolManagerRunnableRunner;
import nro.server.utils.factory.IDFactory;
import nro.server.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.TimeZone;

/**
 * @Author Arriety
 * @see <a href="https://github.com/arrietybeu">github.com/arrietybeu</a>
 */
public class GameServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameServer.class);

    public static final int START_TIME_SECONDS = (int) (ManagementFactory.getRuntimeMXBean().getStartTime() / 1000);

    @Getter
    private static NioServer nioServer;

    public static void main(String[] args) {
        try {
            initUtilityServicesAndConfig();
            DatabaseFactory.init();
            //noinspection ResultOfMethodCallIgnored
            DataManager.getInstance();
            IDFactory.getInstance();
            World.getInstance();
            BannedIpController.start();

            intEntityComponentSystem();

            System.gc();
            nioServer = initNioServer();
            Runtime.getRuntime().addShutdownHook(ShutdownHook.getInstance());
            LOGGER.info("Game server started successfully. Listening on: {}", NetworkConfig.CLIENT_SOCKET_ADDRESS);
        } catch (Throwable e) {
            LOGGER.error("Error : {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    private static NioServer initNioServer() {
        ServerCfg serverCfg = new ServerCfg(NetworkConfig.CLIENT_SOCKET_ADDRESS, "Nro game clients", new GameConnectionFactory());
        NioServer nioServer = new NioServer(NetworkConfig.NIO_READ_WRITE_THREADS, serverCfg);
        nioServer.connect(ThreadPoolManager.getInstance());
        return nioServer;
    }

    private static void intEntityComponentSystem() {
        WorldConfigurationBuilder builder = new WorldConfigurationBuilder();
        builder.with(new FashionUpdateSystem(), new MovementSystem()); // add systems here if needed
        GameWorld gameWorld = GameWorld.getInstance();
        gameWorld.initialize(builder);
        gameWorld.start();

        LOGGER.info("ECS Game World started successfully.");
    }

    private static void initUtilityServicesAndConfig() {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
        Config.load();

        ServerPacketsCommand.init(PacketConfig.SERVER_PACKET_COMMAND);
        NroClientPacketFactory.init(PacketConfig.CLIENT_PACKET_COMMAND);

        //noinspection ResultOfMethodCallIgnored
        ThreadPoolManager.getInstance();

        // Initialize scanner
        Thread.startVirtualThread(CommandService::ActiveCommandLine);

        CronService.initSingleton(ThreadPoolManagerRunnableRunner.class, TimeZone.getTimeZone(ConfigServer.TIME_ZONE_ID));

        LOGGER.info("Game server started in {} seconds.", System.currentTimeMillis() / 1000 - START_TIME_SECONDS);
    }

    public static void shutdownNioServer() {
        if (nioServer != null) {
            nioServer.shutdown();
            nioServer = null;
        }
    }

    public static boolean isShutdownScheduled() {
        return ShutdownHook.getInstance().isRunning();
    }

    public static void initShutdown(int exitCode, int delaySeconds) {
        ShutdownHook.getInstance().initShutdown(exitCode, delaySeconds);
    }

    public static boolean isShuttingDownSoon() {
        return ShutdownHook.getInstance().isRunning() && ShutdownHook.getInstance().getRemainingSeconds() <= 30;
    }

}
