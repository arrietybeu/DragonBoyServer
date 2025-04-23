package nro.utils;

import nro.commons.utils.ExitCode;
import nro.commons.utils.concurrent.DeadLockDetector;
import nro.commons.utils.concurrent.NroRejectedExecutionHandler;
import nro.commons.utils.concurrent.PriorityThreadFactory;
import nro.commons.utils.concurrent.RunnableWrapper;
import nro.server.config.main.ThreadConfig;
import nro.server.system.LogServer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public final class ThreadPoolManager implements Executor {

    private final ScheduledThreadPoolExecutor scheduledPool;
    private final ThreadPoolExecutor instantPool;
    private final ThreadPoolExecutor longRunningPool;

    private ThreadPoolManager() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int instantPoolSize = Math.max(4, ThreadConfig.BASE_THREAD_POOL_SIZE == 0 ? availableProcessors : ThreadConfig.BASE_THREAD_POOL_SIZE);
        int scheduledPoolSize = Math.max(4, ThreadConfig.SCHEDULED_THREAD_POOL_SIZE == 0 ? availableProcessors : ThreadConfig.SCHEDULED_THREAD_POOL_SIZE);

        DeadLockDetector.start(Duration.ofMinutes(1), () -> System.exit(ExitCode.RESTART));
        instantPool = new ThreadPoolExecutor(instantPoolSize, instantPoolSize, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100000),
                new PriorityThreadFactory("InstantPool", ThreadConfig.USE_PRIORITIES ? 7 : Thread.NORM_PRIORITY));
        instantPool.setRejectedExecutionHandler(new NroRejectedExecutionHandler());
        instantPool.prestartAllCoreThreads();

        scheduledPool = new ScheduledThreadPoolExecutor(scheduledPoolSize);
        scheduledPool.setRejectedExecutionHandler(new NroRejectedExecutionHandler());
        scheduledPool.prestartAllCoreThreads();
        scheduledPool.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);

        longRunningPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        LogServer.LogInfo("ThreadPoolManager: Initialized with " + instantPool.getPoolSize() + " instant, " + scheduledPool.getPoolSize() + " scheduler and "
                + longRunningPool.getPoolSize() + " long running threads");
    }

    public ScheduledFuture<?> schedule(Runnable r, long delay, TimeUnit unit) {
        r = new RunnableWrapper(r, ThreadConfig.MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING, true);
        return scheduledPool.schedule(r, delay, unit);
    }

    public ScheduledFuture<?> schedule(Runnable r, long delay) {
        return schedule(r, delay, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable r, long delay, long period) {
        r = new RunnableWrapper(r, ThreadConfig.MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING, true);
        return scheduledPool.scheduleAtFixedRate(r, delay, period, TimeUnit.MILLISECONDS);
    }

    public void executeLongRunning(Runnable r) {
        longRunningPool.execute(new RunnableWrapper(r));
    }

    public Future<?> submit(Runnable r) {
        return instantPool.submit(new RunnableWrapper(r, ThreadConfig.MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING, false));
    }

    public Future<?> submitLongRunning(Runnable r) {
        return longRunningPool.submit(new RunnableWrapper(r, Long.MAX_VALUE, false));
    }


    @Override
    public void execute(Runnable command) {
        instantPool.execute(new RunnableWrapper(command, ThreadConfig.MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING, true));
    }

    public void shutdown() {
        final long begin = System.currentTimeMillis();

        LogServer.LogInfo("ThreadPoolManager: Shutting down.");
        LogServer.LogInfo("\t... executing " + scheduledPool.getActiveCount() + "/" + getTaskCount(scheduledPool) + " scheduled tasks.");
        LogServer.LogInfo("\t... executing " + getTaskCount(instantPool) + " instant tasks.");
        LogServer.LogInfo("\t... executing " + getTaskCount(longRunningPool) + " long running tasks.");

        scheduledPool.shutdown();
        instantPool.shutdown();
        longRunningPool.shutdown();

        boolean success = false;
        try {
            success = awaitTermination(5000);
        } catch (InterruptedException ignored) {
        }

        LogServer.LogInfo("\t... success: " + success + " in " + (System.currentTimeMillis() - begin) + " msec.");
        LogServer.LogInfo("\t... " + getTaskCount(scheduledPool) + " scheduled tasks left.");
        LogServer.LogInfo("\t... " + getTaskCount(instantPool) + " instant tasks left.");
        LogServer.LogInfo("\t... " + getTaskCount(longRunningPool) + " long running tasks left.");
    }


    private int getTaskCount(ThreadPoolExecutor tp) {
        return tp.getQueue().size() + tp.getActiveCount();
    }

    public List<String> getStats() {
        List<String> list = new ArrayList<>();

        list.add("");
        list.add("Scheduled pool:");
        list.add("=================================================");
        list.add("\tgetActiveCount: ...... " + scheduledPool.getActiveCount());
        list.add("\tgetCorePoolSize: ..... " + scheduledPool.getCorePoolSize());
        list.add("\tgetPoolSize: ......... " + scheduledPool.getPoolSize());
        list.add("\tgetLargestPoolSize: .. " + scheduledPool.getLargestPoolSize());
        list.add("\tgetMaximumPoolSize: .. " + scheduledPool.getMaximumPoolSize());
        list.add("\tgetCompletedTaskCount: " + scheduledPool.getCompletedTaskCount());
        list.add("\tgetQueuedTaskCount: .. " + scheduledPool.getQueue().size());
        list.add("\tgetTaskCount: ........ " + scheduledPool.getTaskCount());
        list.add("");
        list.add("Instant pool:");
        list.add("=================================================");
        list.add("\tgetActiveCount: ...... " + instantPool.getActiveCount());
        list.add("\tgetCorePoolSize: ..... " + instantPool.getCorePoolSize());
        list.add("\tgetPoolSize: ......... " + instantPool.getPoolSize());
        list.add("\tgetLargestPoolSize: .. " + instantPool.getLargestPoolSize());
        list.add("\tgetMaximumPoolSize: .. " + instantPool.getMaximumPoolSize());
        list.add("\tgetCompletedTaskCount: " + instantPool.getCompletedTaskCount());
        list.add("\tgetQueuedTaskCount: .. " + instantPool.getQueue().size());
        list.add("\tgetTaskCount: ........ " + instantPool.getTaskCount());
        list.add("");
        list.add("Long running pool:");
        list.add("=================================================");
        list.add("\tgetActiveCount: ...... " + longRunningPool.getActiveCount());
        list.add("\tgetCorePoolSize: ..... " + longRunningPool.getCorePoolSize());
        list.add("\tgetPoolSize: ......... " + longRunningPool.getPoolSize());
        list.add("\tgetLargestPoolSize: .. " + longRunningPool.getLargestPoolSize());
        list.add("\tgetMaximumPoolSize: .. " + longRunningPool.getMaximumPoolSize());
        list.add("\tgetCompletedTaskCount: " + longRunningPool.getCompletedTaskCount());
        list.add("\tgetQueuedTaskCount: .. " + longRunningPool.getQueue().size());
        list.add("\tgetTaskCount: ........ " + longRunningPool.getTaskCount());

        return list;
    }

    private boolean awaitTermination(long timeoutInMillisec) throws InterruptedException {
        final long begin = System.currentTimeMillis();

        while (System.currentTimeMillis() - begin < timeoutInMillisec) {
            if (!scheduledPool.awaitTermination(10, TimeUnit.MILLISECONDS))
                continue;

            if (!instantPool.awaitTermination(10, TimeUnit.MILLISECONDS))
                continue;

            if (!longRunningPool.awaitTermination(10, TimeUnit.MILLISECONDS))
                continue;

            return true;
        }

        return false;
    }

    private static final class SingletonHolder {
        private static final ThreadPoolManager INSTANCE = new ThreadPoolManager();
    }

    public static ThreadPoolManager getInstance() {
        return SingletonHolder.INSTANCE;
    }


}
