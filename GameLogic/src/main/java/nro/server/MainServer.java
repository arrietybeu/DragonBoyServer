/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import nro.network.Session;
import nro.server.config.ConfigServer;
import nro.consts.ConstsCmd;
import nro.controller.Controller;
import nro.repositories.DatabaseConnectionPool;
import nro.server.manager.Manager;
import nro.service.CommandService;
import nro.server.manager.SessionManager;
import org.slf4j.LoggerFactory;

/**
 * @author Arriety
 */
public class MainServer {

    private static ServerSocket server;
    private static final Controller controller = new Controller();
    private static MainServer instance;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(2);
    private volatile boolean running;

    public static MainServer gI() {
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
            Config();
            ActiveCommandLine();
            ActiveDeadLockDetector();
            MainServer.gI().run();
        } catch (Exception e) {
            LogServer.LogException("Error main: " + e.getMessage());
        }
    }

    public void run() {
        try {
            this.running = true;
            this.startGame();
            this.startServerSocket();// tao 1 luong quan ly ket noi, 1 session = 2 luong
        } catch (Exception e) {
            LogServer.LogException("Error run: " + e.getMessage());
        }
    }

    private void startServerSocket() {
//        threadPool.execute(() -> {
            if (this.getSocket() != null && !this.getSocket().isClosed()) {
                LogServer.DebugLogic("Server is already running.");
                return;
            }
            try {
                this.setSocket(new ServerSocket(ConfigServer.PORT));
                LogServer.DebugLogic("Server started at port [" + ConfigServer.PORT + "]");

                while (this.running && this.getSocket() != null && !this.getSocket().isClosed()) {
                    try {
                        new Session(this.getSocket().accept(), controller);
                    } catch (SocketException e) {
                        if (!this.running) {
                            LogServer.DebugLogic("Server stopped gracefully.");
                        } else {
                            LogServer.LogException("Socket exception: " + e.getMessage());
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
                try {
                    if (this.getSocket() != null && !this.getSocket().isClosed()) {
                        this.getSocket().close();
                    }
                    LogServer.DebugLogic("Server socket closed.");
                } catch (Exception e) {
                    LogServer.LogException("Error closing server: " + e.getMessage());
                }
            }
//        });
    }

    private void startGame() {
        try {
            SessionManager.gI().startSessionChecker();
        } catch (Exception e) {
            LogServer.LogException("Error startGame: " + e.getMessage());
        }
    }

    private static void ActiveCommandLine() {
        threadPool.execute(CommandService::ActiveCommandLine);
    }

    private static void ActiveDeadLockDetector() {
        DeadLockDetector deadLockDetector = new DeadLockDetector(Duration.ofSeconds(2L), () -> {
            System.out.println("isRestartOnDeadLock");
        });
        deadLockDetector.start();
    }

    public void close() {
        try {
            this.running = false;
            this.closeServerSocket();
            SessionManager.gI().kickAllPlayer("Bảo trì");
            DatabaseConnectionPool.closeConnections();
            LogServer.DebugLogic("Server closed");
            System.exit(0);
        } catch (Exception e) {
            LogServer.LogException("Error close: " + e.getMessage());
        } finally {
            instance = null;
        }
    }

    // close server socket
    private void closeServerSocket() {
        try {
            if (this.getSocket() != null && !this.getSocket().isClosed()) {
                this.getSocket().close();
            }
        } catch (Exception e) {
            LogServer.LogException("Error closeServerSocket: " + e.getMessage());
        }
    }

    public static void Config() {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        ((Logger) LoggerFactory.getLogger("org.reflections")).setLevel(Level.ERROR);
        ((Logger) LoggerFactory.getLogger("com.zaxxer.hikari")).setLevel(Level.ERROR);
    }

    public ServerSocket getSocket() {
        return server;
    }

    public synchronized void setSocket(ServerSocket server) {
        MainServer.server = server;
    }
}
