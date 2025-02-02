package nro.utils;

import nro.network.Session;
import nro.server.manager.SessionManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Util {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void kickSessionWithDelay(Session session, int delaySeconds) {
        new Thread(() -> {
            try {
                Thread.sleep(delaySeconds);
            } catch (InterruptedException ignored) {
            }
            SessionManager.getInstance().kickSession(session);
        }).start();
    }

    public static void getMethodCaller() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTrace[3];
        System.out.println("Called by: " + caller);
    }

}
