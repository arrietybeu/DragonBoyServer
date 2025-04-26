package nro.server;

import nro.commons.network.NioServer;
import nro.commons.network.ServerCfg;
import nro.commons.utils.SystemInfo;
import nro.server.configs.Config;
import nro.server.configs.network.NetworkConfig;
import nro.server.network.nro.GameConnectionFactory;
import nro.server.utils.LogServer;
import nro.server.utils.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameServer.class);

    private static NioServer nioServer;

    public static void main(String[] args) {
        initUtilityServicesAndConfig();

        System.gc();

        SystemInfo.logAll();
        nioServer = initNioServer();
        Runtime.getRuntime().addShutdownHook(ShutdownHook.getInstance());
    }

    private static NioServer initNioServer() {
        ServerCfg serverCfg = new ServerCfg(NetworkConfig.CLIENT_SOCKET_ADDRESS, "Nro game clients", new GameConnectionFactory());
        NioServer nioServer = new NioServer(NetworkConfig.NIO_READ_WRITE_THREADS, serverCfg);
        nioServer.connect(ThreadPoolManager.getInstance());
        return nioServer;
    }

    public static void shutdownNioServer() {
        if (nioServer != null) {
            nioServer.shutdown();
            nioServer = null;
        }
    }

    private static void initUtilityServicesAndConfig() {
        Config.load();
        ThreadPoolManager pool = ThreadPoolManager.getInstance();
        pool.getStats().forEach(line -> LOGGER.info(LogServer.ANSI_GREEN + "{}" + LogServer.ANSI_RESET, line));
    }

}
