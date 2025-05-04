package nro.commons.utils.concurrent;

import nro.commons.utils.ExitCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(UncaughtExceptionHandler.class);

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        log.error("Uncaught exception in thread [{}]:", t.getName(), e);

        if (e instanceof OutOfMemoryError) {
            log.error("OutOfMemoryError detected. Exiting with code 100...");
            Thread.startVirtualThread(() -> System.exit(ExitCode.RESTART)); // async since System.exit indefinitely blocks the calling thread
        } else if (e instanceof LinkageError) {
            log.error("LinkageError: Class version conflict detected.");
        }

        if (isMainThread(t) && hasNonDaemonThreads(t)) {
            log.error("Main thread crashed. Exiting with code 1...");
            Thread.startVirtualThread(() -> System.exit(ExitCode.ERROR)); // async since System.exit indefinitely blocks the calling thread
        }
    }


    private boolean isMainThread(Thread t) {
        return t.threadId() == 1; // main thread luôn có ID = 1
    }

    private boolean anyExitBlockingThread(Thread ignoredThread) {
        return Thread.getAllStackTraces().keySet().stream().anyMatch(lt -> lt != ignoredThread && !lt.isDaemon());
    }

    private boolean hasNonDaemonThreads(Thread ignoredThread) {
        return Thread.getAllStackTraces().keySet().stream()
                .anyMatch(th -> th != ignoredThread && !th.isDaemon());
    }

}
