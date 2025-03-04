package nro.utils;

import nro.model.monster.Monster;
import nro.model.player.Player;

import java.util.SplittableRandom;

public class Util {

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

    public static int getDistance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static int getDistance(Player player, Monster monster) {
        return getDistance(player.getX(), player.getY(), monster.getX(), monster.getY());
    }

    public static boolean canDoWithTime(long lastTime, long miniTimeTarget) {
        return System.currentTimeMillis() - lastTime > miniTimeTarget;
    }

    public static void delay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
