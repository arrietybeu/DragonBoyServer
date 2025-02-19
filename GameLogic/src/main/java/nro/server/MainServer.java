package nro.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import nro.controller.Controller;
import nro.consts.ConstsCmd;
import nro.server.network.Session;
import nro.repositories.DatabaseConnectionPool;
import nro.server.config.ConfigServer;
import nro.server.manager.Manager;
import nro.server.manager.SessionManager;
import nro.service.CommandService;
import org.slf4j.LoggerFactory;

public class MainServer {

    private ServerSocket serverSocket;
    private final Controller controller = new Controller();
    private static MainServer instance;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(2);
    private volatile boolean running;

    public static synchronized MainServer getInstance() {
        if (instance == null) {
            instance = new MainServer();
            instance.init();
        }
        return instance;
    }

    private void init() {
        Manager.getInstance();
        ConstsCmd.addListMsg();
    }

    public static void main(String[] args) {
        try {
            configure();
            startCommandLine();
            startDeadLockDetector();
            MainServer.getInstance().start();
        } catch (Exception e) {
            LogServer.LogException("Error main: " + e.getMessage());
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
        try {
            this.serverSocket = new ServerSocket(ConfigServer.PORT);
            LogServer.DebugLogic("Server started at port [" + ConfigServer.PORT + "]");

            while (this.running && this.serverSocket != null && !this.serverSocket.isClosed()) {
                try {
                    Socket clientSocket = this.serverSocket.accept();
                    new Session(clientSocket, controller);
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
            System.exit(0);
        } finally {
            closeServerSocket();
        }
    }

    private void startGame() {
        try {
            SessionManager.getInstance().startSessionChecker();
        } catch (Exception e) {
            LogServer.LogException("Error startGame: " + e.getMessage());
        }
    }

    private static void startCommandLine() {
        threadPool.execute(CommandService::ActiveCommandLine);
    }

    private static void startDeadLockDetector() {
        DeadLockDetector deadLockDetector = new DeadLockDetector(Duration.ofSeconds(2L), () -> {
            System.out.println("isRestartOnDeadLock");
        });
        deadLockDetector.start();
    }

    public void shutdown() {
        try {
            this.running = false;
            closeServerSocket();
            SessionManager.getInstance().kickAllPlayer("Bảo trì");
            Manager.getInstance().clearAllData();
            DatabaseConnectionPool.closeConnections();
            LogServer.DebugLogic("Server closed");
            System.exit(0);
        } catch (Exception e) {
            LogServer.LogException("Error shutdown: " + e.getMessage());
        } finally {
            instance = null;
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
