package nro.utils;

import java.util.SplittableRandom;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Util {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static final SplittableRandom random = new SplittableRandom();

    public static void getMethodCaller() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTrace[3];
        System.out.println("Called by: " + caller);
    }

    public static int nextInt(int bound) {
        return random.nextInt(bound);
    }

    public static int nextInt(int origin, int bound) {
        return random.nextInt(origin, bound);
    }

}
