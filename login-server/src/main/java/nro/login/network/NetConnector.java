package nro.login.network;

import nro.commons.network.NioServer;
import nro.commons.network.ServerCfg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetConnector {

    private final static NioServer instance;
    private final static ExecutorService dcExecutor = Executors.newCachedThreadPool();

    static {
        ServerCfg aion = new ServerCfg(Config.CLIENT_SOCKET_ADDRESS, "Nro game clients", LoginConnection::new);
        ServerCfg gs = new ServerCfg(Config.GAMESERVER_SOCKET_ADDRESS, "game servers", GsConnection::new);
        instance = new NioServer(Config.NIO_READ_WRITE_THREADS, aion, gs);
    }

    public static void connect() {
        instance.connect(dcExecutor);
    }

    public static void shutdown() {
        instance.shutdown();
        dcExecutor.shutdown();
    }

}
