import nro.commons.utils.ExitCode;
import nro.commons.utils.concurrent.DeadLockDetector;

import java.time.Duration;
import java.util.stream.Stream;

public class DeadlockTest {

    private static final Object lock1 = new Object();
    private static final Object lock2 = new Object();

    public static void main(String... args) {
        DeadLockDetector.start(Duration.ofSeconds(2), () -> System.exit(ExitCode.ERROR));
        createDeadlock();
    }

    private static void createDeadlock() {
        synchronized (lock1) {
            // the stream is just to generate a longer stack trace
            Stream.of("").limit(1).forEach(ignore -> {
                new Thread(() -> {
                    System.out.println("Locking lock 2 from thread 2");
                    synchronized (lock2) {
                        System.out.println("Deadlocking");
                        synchronized (lock1) {
                            System.out.println("This will not be printed");
                        }
                    }
                }).start();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
                synchronized (lock2) {
                    System.out.println("This will not be printed");
                }
            });
        }
    }
}
