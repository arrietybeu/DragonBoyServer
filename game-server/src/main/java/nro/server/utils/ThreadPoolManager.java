package nro.server.utils;

import nro.commons.utils.ExitCode;
import nro.commons.utils.concurrent.DeadLockDetector;
import nro.commons.utils.concurrent.NroRejectedExecutionHandler;
import nro.commons.utils.concurrent.PriorityThreadFactory;
import nro.commons.utils.concurrent.RunnableWrapper;
import nro.server.configs.main.ThreadConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public final class ThreadPoolManager implements Executor {

    private static final Logger log = LoggerFactory.getLogger(ThreadPoolManager.class);
    /**
     * xử lý các tác vụ định kỳ
     */
    private final ScheduledThreadPoolExecutor scheduledPool;

    /**
     * xử lý các tác vụ tức thì
     */
    private final ThreadPoolExecutor instantPool;

    /**
     * xử lý tác vụ trong khoảng thời gian lâu
     */
    private final ThreadPoolExecutor longRunningPool;

    private ThreadPoolManager() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int instantPoolSize = Math.max(4, ThreadConfig.BASE_THREAD_POOL_SIZE == 0 ? availableProcessors : ThreadConfig.BASE_THREAD_POOL_SIZE);
        int scheduledPoolSize = Math.max(4, ThreadConfig.SCHEDULED_THREAD_POOL_SIZE == 0 ? availableProcessors : ThreadConfig.SCHEDULED_THREAD_POOL_SIZE);

        DeadLockDetector.start(Duration.ofMinutes(1), () -> System.exit(ExitCode.RESTART));
        instantPool = new ThreadPoolExecutor(instantPoolSize, instantPoolSize, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100_000),
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

    /**
     * <code>schedule</code >1 task chạy 1 lần sau một khoảng delay, dùng {@link ScheduledThreadPoolExecutor}.
     * Thường dùng cho các tác vụ trì hoãn như: hồi skill, hồi sinh sau bao nhiêu s, timeout...
     *
     * @param r
     * @param delay
     * @param unit
     * @return {@link RunnableScheduledFuture}
     */
    public ScheduledFuture<?> schedule(Runnable r, long delay, TimeUnit unit) {
        r = new RunnableWrapper(r, ThreadConfig.MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING, true);
        return scheduledPool.schedule(r, delay, unit);
    }

    /**
     * Giống hàm <code>schedule</code> ở trên nhưng mặc định đơn vị delay là MILLISECONDS.
     * Cho phép viết gọn hơn Ví dụ: <code>schedule(task, 500)</code>; sẽ hiểu là 500ms.
     *
     * @param r
     * @param delay
     * @return {@link RunnableScheduledFuture}
     */
    public ScheduledFuture<?> schedule(Runnable r, long delay) {
        return schedule(r, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Lên lịch task chạy định kỳ: delay lần đầu, sau đó cứ mỗi `period` ms thì chạy lại 1 lần.
     * Thường dùng cho các task định kỳ như: kiểm tra trạng thái boss, quét item rơi, tính buff/debuff,...
     *
     * @param r
     * @param delay
     * @param period
     * @return {@link RunnableScheduledFuture}
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable r, long delay, long period) {
        r = new RunnableWrapper(r, ThreadConfig.MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING, true);
        return scheduledPool.scheduleAtFixedRate(r, delay, period, TimeUnit.MILLISECONDS);
    }

    /**
     * Dành cho các task nặng, chạy lâu (load map, load data lớn, import file...).
     * Dùng {@link ExecutorService} (cachedThreadPool) -> tạo luồng mới nếu cần mà không giới hạn cứng số lượng.
     *
     * @param r
     */
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

    private int getTaskCount(ThreadPoolExecutor tp) {
        return tp.getQueue().size() + tp.getActiveCount();
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

        log.info("\t... success: {} in {} msec.", success, System.currentTimeMillis() - begin);
        log.info("\t... {} scheduled tasks left.", getTaskCount(scheduledPool));
        log.info("\t... {} instant tasks left.", getTaskCount(instantPool));
        log.info("\t... {} long running tasks left.", getTaskCount(longRunningPool));
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

    public List<String> getStats() {
        List<String> list = new ArrayList<>();

        list.add("");
        list.add("Scheduled pool (Luồng xử lý định kỳ):");
        list.add("=================================================");
        list.add("\tSố luồng đang thực thi tác vụ: ................. " + scheduledPool.getActiveCount());
        list.add("\tSố luồng lõi luôn tồn tại: ..................... " + scheduledPool.getCorePoolSize());
        list.add("\tTổng số luồng hiện tại (đang chạy hoặc chờ): ... " + scheduledPool.getPoolSize());
        list.add("\tSố luồng cao nhất từng đạt: .................... " + scheduledPool.getLargestPoolSize());
        list.add("\tSố luồng tối đa cho phép: ...................... " + scheduledPool.getMaximumPoolSize());
        list.add("\tTổng số tác vụ đã hoàn thành: .................. " + scheduledPool.getCompletedTaskCount());
        list.add("\tSố tác vụ đang chờ xử lý: ...................... " + scheduledPool.getQueue().size());
        list.add("\tTổng số tác vụ đã submit: ...................... " + scheduledPool.getTaskCount());

        list.add("");
        list.add("Instant pool (Luồng xử lý tác vụ tức thì):");
        list.add("=================================================");
        list.add("\tSố luồng đang thực thi tác vụ: ................. " + instantPool.getActiveCount());
        list.add("\tSố luồng lõi luôn tồn tại: ..................... " + instantPool.getCorePoolSize());
        list.add("\tTổng số luồng hiện tại (đang chạy hoặc chờ): ... " + instantPool.getPoolSize());
        list.add("\tSố luồng cao nhất từng đạt: .................... " + instantPool.getLargestPoolSize());
        list.add("\tSố luồng tối đa cho phép: ...................... " + instantPool.getMaximumPoolSize());
        list.add("\tTổng số tác vụ đã hoàn thành: .................. " + instantPool.getCompletedTaskCount());
        list.add("\tSố tác vụ đang chờ xử lý: ...................... " + instantPool.getQueue().size());
        list.add("\tTổng số tác vụ đã submit: ...................... " + instantPool.getTaskCount());

        list.add("");
        list.add("Long running pool (Luồng xử lý tác vụ dài hạn):");
        list.add("=================================================");
        list.add("\tSố luồng đang thực thi tác vụ: ................. " + longRunningPool.getActiveCount());
        list.add("\tSố luồng lõi luôn tồn tại: ..................... " + longRunningPool.getCorePoolSize());
        list.add("\tTổng số luồng hiện tại (đang chạy hoặc chờ): ... " + longRunningPool.getPoolSize());
        list.add("\tSố luồng cao nhất từng đạt: .................... " + longRunningPool.getLargestPoolSize());
        list.add("\tSố luồng tối đa cho phép: ...................... " + longRunningPool.getMaximumPoolSize());
        list.add("\tTổng số tác vụ đã hoàn thành: .................. " + longRunningPool.getCompletedTaskCount());
        list.add("\tSố tác vụ đang chờ xử lý: ...................... " + longRunningPool.getQueue().size());
        list.add("\tTổng số tác vụ đã submit: ...................... " + longRunningPool.getTaskCount());
        return list;
    }


    private static final class SingletonHolder {
        private static final ThreadPoolManager INSTANCE = new ThreadPoolManager();
    }

    public static ThreadPoolManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

}
