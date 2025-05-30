package nro.server;

import ch.qos.logback.classic.LoggerContext;
import nro.commons.services.CronService;
import nro.commons.services.cron.CurrentThreadRunnableRunner;
import nro.commons.utils.ExitCode;
import nro.commons.utils.concurrent.RunnableStatsManager;
import nro.server.configs.main.ShutdownConfig;
import nro.server.utils.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class ShutdownHook extends Thread {

    private static final Logger log = LoggerFactory.getLogger(ShutdownHook.class);

    private static final int UNSET_DELAY = Integer.MIN_VALUE;
    private final AtomicInteger remainingSeconds = new AtomicInteger(UNSET_DELAY);

    public static ShutdownHook getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static final class SingletonHolder {
        private static final ShutdownHook INSTANCE = new ShutdownHook();
    }

    private ShutdownHook() {
        if (ShutdownConfig.RESTART_SCHEDULE != null) {
            // CurrentThreadRunnableRunner, otherwise ThreadPoolManager.getInstance().shutdown() will try to wait for this cron task
            CronService.getInstance().schedule(() -> System.exit(ExitCode.RESTART), CurrentThreadRunnableRunner.class, ShutdownConfig.RESTART_SCHEDULE, true);
            log.info("Scheduled automatic server restart based on cron expression: {}", ShutdownConfig.RESTART_SCHEDULE);
        }
    }

    @Override
    public void run() {
        // this method is run when System.exit is triggered, or via other external events like console CTRL+C
        remainingSeconds.compareAndSet(UNSET_DELAY, ShutdownConfig.DELAY);
        for (int announceInterval = 1, expectedSeconds = remainingSeconds.get(); remainingSeconds.get() > 0; ) {
            try {
                /**
                 * nếu không có người chơi sẽ close sớm hơn
                 * còn không thì sẽ gửi thông báo từng giây bảo trì
                 */
//                if (World.getInstance().getAllPlayers().isEmpty())
//                    break; // fast exit
//
                if (remainingSeconds.get() % announceInterval == 0) {
                    log.info("Runtime is shutting down in " + remainingSeconds + " seconds.");
//                    PacketSendUtility.broadcastToWorld(SM_SYSTEM_MESSAGE.STR_SERVER_SHUTDOWN(remainingSeconds.get()));
                    announceInterval = nextInterval(remainingSeconds.get(), 5, 60);
                }
//
                sleep(1000);
//
//                // if remainingSeconds got updated from another thread
                if (!remainingSeconds.compareAndSet(expectedSeconds, --expectedSeconds)) {
                    expectedSeconds = remainingSeconds.get();
                    announceInterval = 1;
                }
            } catch (InterruptedException ignored) {
            } catch (Exception e) {
                log.error("", e);
            }
        }

        // TODO close tất cả các service

        GameServer.shutdownNioServer(); // shuts down nro.utils.test.network, disconnects cs/ls/all players and saves them

        RunnableStatsManager.dumpClassStats(RunnableStatsManager.SortBy.AVG);// print system
//        PeriodicSaveService.getInstance().onShutdown(); // save data player, status server
//
//        GameTimeService.getInstance().saveGameTime();
        CronService.getInstance().shutdown();
        ThreadPoolManager.getInstance().shutdown();

        // shut down logger factory to flush all pending log messages
        ((LoggerContext) LoggerFactory.getILoggerFactory()).stop();
    }

    protected void initShutdown(int exitCode, int delaySeconds) {
        if (delaySeconds < 0) return;
        // update shutdown delay if possible (unset or more than one second left)
        int previousValue = remainingSeconds.getAndUpdate(seconds -> seconds == UNSET_DELAY
                || seconds > 1 ? delaySeconds : seconds);
        if (previousValue == UNSET_DELAY)
            Thread.startVirtualThread(() -> System.exit(exitCode)); // async since System.exit indefinitely blocks the calling thread
    }

    /**
     * @param remainingSeconds - remaining time in seconds, until the shutdown will be performed
     * @param minInterval      - minimum interval to be returned (minInterval will equal remainingSeconds if remainingSeconds is shorter)
     * @param maxInterval      - maximum interval to be returned
     * @return The interval (in seconds) to wait until the next announce should be sent to all players.
     */
    private static int nextInterval(int remainingSeconds, int minInterval, int maxInterval) {
        if (remainingSeconds < minInterval) minInterval = Math.max(1, remainingSeconds);
        int interval = remainingSeconds / 2;
        interval = interval / 5 * 5; // ensure a "clean" interval (dividable by 5, like 5, 10, 15s and so on)
        return Math.min(maxInterval, Math.max(minInterval, interval));
    }

    protected boolean isRunning() {
        return remainingSeconds.get() != UNSET_DELAY;
    }

    protected int getRemainingSeconds() {
        return remainingSeconds.get();
    }


}
