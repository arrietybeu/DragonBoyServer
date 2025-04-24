package test;

import nro.commons.utils.ExitCode;
import nro.commons.utils.concurrent.DeadLockDetector;
import nro.server.utils.ThreadPoolManager;

import java.time.Duration;

public class DeadlockTest {

    private static final Object LOCK_A = new Object();
    private static final Object LOCK_B = new Object();

    public static void main(String... args) {
        DeadLockDetector.start(Duration.ofSeconds(2), () -> System.exit(ExitCode.ERROR));
        createDeadlock();
    }

    public static void createDeadlock() {
        // Task 1: khóa A → đợi B
        Runnable task1 = () -> {
            synchronized (LOCK_A) {
                System.out.println("ThreadPool - Task 1: locked A");
                try {
//                    Thread.sleep(100);
                } catch (Exception ignored) {
                }
                synchronized (LOCK_B) {
                    System.out.println("ThreadPool - Task 1: locked B");
                }
            }
        };

        // Task 2: khóa B → đợi A
        Runnable task2 = () -> {
            synchronized (LOCK_B) {
                System.out.println("ThreadPool - Task 2: locked B");
                try {
//                    Thread.sleep(100);
                } catch (Exception ignored) {
                }
                synchronized (LOCK_A) {
                    System.out.println("ThreadPool - Task 2: locked A");
                }
            }
        };

        // Ném task vào ThreadPoolManager (instant pool)
        ThreadPoolManager pool = ThreadPoolManager.getInstance();
        pool.submit(task1);
        pool.submit(task2);
    }
}
