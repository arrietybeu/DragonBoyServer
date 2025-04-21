package nro.utils;

import java.text.Normalizer;
import java.util.SplittableRandom;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Util {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(25);

    public static void delay(int delayTime, Runnable callback) {
        scheduler.schedule(callback, delayTime, TimeUnit.SECONDS);
    }

    public static void getMethodCaller() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTrace[3];
        System.out.println("Called by: " + caller);
    }

    public static int getDistance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static boolean canDoWithTime(long lastTime, long miniTimeTarget) {
        return System.currentTimeMillis() - lastTime > miniTimeTarget;
    }

    public static String numberToString(long number) {
        if (number == 0)
            return "0";

        boolean isNegative = number < 0;
        number = Math.abs(number);

        String suffix = "";
        double displayNumber = number;

        if (number >= 1_000_000_000L) {
            suffix = "Tỉ";
            displayNumber /= 1_000_000_000.0;
        } else if (number >= 1_000_000L) {
            suffix = "Tr";
            displayNumber /= 1_000_000.0;
        } else if (number >= 1_000L) {
            suffix = "k";
            displayNumber /= 1_000.0;
        } else {
            return (isNegative ? "-" : "") + number;
        }

        String formattedNumber = String.format("%.2f", displayNumber).replaceAll("\\.?0+$", "");

        return (isNegative ? "-" : "") + formattedNumber + suffix;
    }

    public static String removeDiacritics(String text) {
        if (text == null) return "";
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }
}
