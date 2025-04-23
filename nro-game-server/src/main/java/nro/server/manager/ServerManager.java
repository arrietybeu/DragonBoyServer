package nro.server.manager;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import nro.commons.network.NioServer;
import nro.server.ShutdownHook;
import nro.server.controller.MessageController;
import nro.consts.ConstsCmd;
import nro.server.controller.MessageProcessorRegistry;
import nro.server.realtime.core.DispatcherRegistry;
import nro.server.service.model.skill.behavior.SkillBehaviorRegistry;
import nro.server.system.LogServer;
import nro.server.service.core.usage.ItemHandlerRegistry;
import nro.server.network.Session;
import nro.commons.database.DatabaseFactory;
import nro.server.config.ConfigServer;
import nro.server.service.core.system.CommandService;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.ClassicConstants;

public final class ServerManager {

    private ServerSocket serverSocket;
    private final MessageController messageController = new MessageController();
    private static ServerManager instance;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(3);
    private volatile boolean running;

    private static NioServer nioServer;

    public static ServerManager getInstance() {
        if (instance == null) {
            instance = new ServerManager();
            instance.init();
        }
        return instance;
    }

    static {
        System.setProperty(ClassicConstants.CONFIG_FILE_PROPERTY, "config/logback.xml"); // must be set before instantiating any logger
//        archiveLogs(); // must also run before instantiating any logger
    }

    private void init() {
        Manager.getInstance();
        ConstsCmd.addListMsg();
        ItemHandlerRegistry.init(ConfigServer.PATH_USE_ITEM_HANDLER);
        MessageProcessorRegistry.init(ConfigServer.PATH_CONTROLLER_HANDLER);
        DispatcherRegistry.startAllDispatchers(ConfigServer.PATH_ENTITY_COMPONENT_SYSTEM);
        SkillBehaviorRegistry.init(ConfigServer.PATH_SKILLS_HANDLER);
    }

    public static void launch() {
        try {
            configure();
            startCommandLine();
            ServerManager.getInstance().start();
            Runtime.getRuntime().addShutdownHook(ShutdownHook.getInstance());
        } catch (Exception e) {
            LogServer.LogException("Error main: " + e.getMessage(), e);
        }
    }

    public void start() {
        try {
            this.running = true;
            startGame();
            startServerSocket();
        } catch (Exception e) {
            LogServer.LogException("Error start: " + e.getMessage());
        }
    }

    private void startServerSocket() {
        if (this.serverSocket != null && !this.serverSocket.isClosed()) {
            LogServer.DebugLogic("Server is already running.");
            return;
        }
        threadPool.execute(() -> {
            try {
                this.serverSocket = new ServerSocket(ConfigServer.PORT);
                LogServer.DebugLogic("Server started at port [" + ConfigServer.PORT + "]");
                while (this.running && this.serverSocket != null && !this.serverSocket.isClosed()) {
                    try {
                        new Session(this.serverSocket.accept(), messageController);
                    } catch (SocketException se) {
                        if (!this.running) {
                            LogServer.DebugLogic("Server stopped gracefully.");
                        } else {
                            LogServer.LogException("Socket exception: " + se.getMessage());
                        }
                        break;
                    } catch (Exception e) {
                        LogServer.LogException("Error in client connection: " + e.getMessage());
                    }
                }

            } catch (IOException e) {
                LogServer.LogException("Error starting server: " + e.getMessage());
            } finally {
                closeServerSocket();
            }
        });
    }

    private void startGame() {
        try {
            SessionManager.getInstance().startSessionChecker();
        } catch (Exception e) {
            LogServer.LogException("Error startGame: " + e.getMessage(), e);
        }
    }

    private static void startCommandLine() {
        threadPool.execute(CommandService::ActiveCommandLine);
    }

    public void shutdown() {
        try {
            this.running = false;
            closeServerSocket();
            SessionManager.getInstance().kickAllPlayer("Bảo trì");
            Manager.getInstance().clearAllData();
            DatabaseFactory.closeAll();
            LogServer.DebugLogic("Server closed");
            System.exit(0);
        } catch (Exception e) {
            LogServer.LogException("Error shutdown: " + e.getMessage());
        } finally {
            instance = null;
        }
    }

    public static void shutdownNioServer() {
        if (nioServer != null) {
            nioServer.shutdown();
            nioServer = null;
        }
    }

    private void closeServerSocket() {
        if (this.serverSocket != null && !this.serverSocket.isClosed()) {
            try {
                this.serverSocket.close();
            } catch (IOException e) {
                LogServer.LogException("Error closing server socket: " + e.getMessage());
            }
        }
        LogServer.DebugLogic("Server socket closed.");
    }

    public static void configure() {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        ((Logger) LoggerFactory.getLogger("org.reflections")).setLevel(Level.ERROR);
        ((Logger) LoggerFactory.getLogger("com.zaxxer.hikari")).setLevel(Level.ERROR);
    }
}
