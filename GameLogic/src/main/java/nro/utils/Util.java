package nro.utils;

public class Util {

    public static void getMethodCaller() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTrace[3];
        System.out.println("Called by: " + caller);
    }
}
