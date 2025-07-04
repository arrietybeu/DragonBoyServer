package nro.server.utils;

import nro.commons.utils.ExitCode;
import nro.commons.utils.concurrent.*;
import nro.server.configs.main.ThreadConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.*;

/**
 * @Author Arriety
 */
public final class ThreadPoolManager implements Executor {

    private static final Logger log = LoggerFactory.getLogger(ThreadPoolManager.class);

    private final ScheduledThreadPoolExecutor scheduledExecutor;
    private final ThreadPoolExecutor instantExecutor;
    private final ThreadPoolExecutor longRunningExecutor;

    private ThreadPoolManager() {
        DeadLockDetector.start(Duration.ofMinutes(1), () -> System.exit(ExitCode.RESTART));

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int instantExecutorSize = Math.max(4, ThreadConfig.BASE_THREAD_POOL_SIZE == 0 ? availableProcessors : ThreadConfig.BASE_THREAD_POOL_SIZE);

        int scheduledSize = ThreadConfig.SCHEDULED_THREAD_POOL_SIZE > 0
                ? ThreadConfig.SCHEDULED_THREAD_POOL_SIZE
                : Math.max(4, availableProcessors);

        instantExecutor = instantiateExecutor(instantExecutorSize, instantExecutorSize);
        scheduledExecutor = instantiateScheduledExecutor(scheduledSize);
        longRunningExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        log.info("ThreadPoolManager: Initialized with {} instant, {} scheduler and {} long running threads",
                instantExecutor.getCorePoolSize(), scheduledExecutor.getCorePoolSize(), longRunningExecutor.getPoolSize());

    }

    private ThreadPoolExecutor instantiateExecutor(int corePoolSize, int maximumPoolSize) {
        var executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100_000), new PriorityThreadFactory("InstantPool",
                ThreadConfig.USE_PRIORITIES ? 7 : Thread.NORM_PRIORITY));

        executor.setRejectedExecutionHandler(new NroRejectedExecutionHandler());
        executor.prestartAllCoreThreads();
        return executor;
    }

    private ScheduledThreadPoolExecutor instantiateScheduledExecutor(int corePoolSize) {
        var executor = new ScheduledThreadPoolExecutor(corePoolSize);
        executor.setRejectedExecutionHandler(new NroRejectedExecutionHandler());
        executor.prestartAllCoreThreads();
        executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        return executor;
    }

    /**
     * Lên lịch thực hiện một task sau một khoảng thời gian delay (một lần duy nhất).
     * <p>
     * Chỉ nên dùng cho:
     * - Task nhẹ, non-blocking
     * - Không cần thực hiện định kỳ
     * - Dùng trong logic game như: delay skill, spawn NPC, gửi hiệu ứng, hồi sinh,...
     * <p>
     * Nếu task có I/O hoặc chạy lâu → sử dụng longRunningExecutor.
     *
     * @param task  task cần thực thi
     * @param delay Khoảng thời gian delay
     * @param unit  Đơn vị thời gian
     * @return ScheduledFuture đại diện cho task
     */

    public ScheduledFuture<?> schedule(String name, Runnable task, long delay, TimeUnit unit) {
        Runnable wrapped = monitorWrappedTask(new NamedRunnable(name, task));
        return scheduledExecutor.schedule(wrapped, delay, unit);
    }

    /**
     * Lên lịch thực thi một tác vụ một lần duy nhất sau một khoảng delay tính bằng mili giây.
     * <p>
     * Ví dụ:
     * - Hồi kỹ năng sau 5000ms
     * - Delay triệu hồi pet
     * - Kết thúc hiệu ứng animation sau 300ms
     * <p>
     * ❗ Task nên nhẹ, không blocking I/O.
     * ❗ Không dùng cho task định kỳ (thay vào đó dùng scheduleAtFixedRate).
     */
    public ScheduledFuture<?> schedule(String name, Runnable task, long delayMillis) {
        return schedule(name, task, delayMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Lên lịch chạy một tác vụ định kỳ (fixed rate), nghĩa là mỗi `periodMillis` ms,
     * task sẽ được gọi lại bất kể lần trước mất bao lâu để thực thi.
     * <p>
     * Phù hợp cho:
     * - Tác vụ lặp đều: hồi máu/mana, NPC shout, auto save, kiểm tra trạng thái boss,...
     * <p>
     * ❗ Nếu task có thể mất thời gian dài → cân nhắc dùng longRunningExecutor hoặc scheduleWithFixedDelay().
     * ❗ Task nên không blocking, để không ảnh hưởng đến thread pool định kỳ.
     *
     * @param task         Task cần thực hiện
     * @param initialDelay Thời gian chờ trước lần chạy đầu
     * @param periodMillis Chu kỳ giữa các lần gọi (ms)
     * @return ScheduledFuture đại diện cho task định kỳ
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long periodMillis) {
        Runnable wrapped = monitorWrappedTask(task);
        return scheduledExecutor.scheduleAtFixedRate(wrapped, initialDelay, periodMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Thực thi một tác vụ nặng hoặc có thể blocking (I/O, DB, file...).
     * <p>
     * Dùng khi:
     * - Tác vụ mất nhiều thời gian
     * - Có khả năng blocking hoặc yêu cầu tài nguyên lớn
     * - Không phù hợp chạy trong Virtual Thread hoặc pool tức thì
     * <p>
     * Ví dụ:
     * - Load dữ liệu bản đồ
     * - Gọi truy vấn DB lớn
     * - Chạy batch định kỳ, index dữ liệu
     */
    public void executeLongRunning(Runnable task) {
        longRunningExecutor.execute(monitorWrappedTask(task));
    }

    /**
     * Gửi một tác vụ nặng hoặc blocking (I/O, DB, file...) để thực thi,
     * và trả về một Future đại diện cho tác vụ đó.
     * <p>
     * Dùng khi:
     * - Cần giám sát trạng thái task
     * - Cần cancel hoặc chờ task kết thúc (future.get())
     * - Task không phù hợp với Virtual Thread
     * <p>
     * Ví dụ:
     * - Load dữ liệu từ XML, JSON
     * - Xử lý dữ liệu sau khi unmarshal
     * - Import file lớn
     */
    public Future<?> submitLongRunning(Runnable task) {
        return longRunningExecutor.submit(monitorWrappedTask(task));
    }

    public Future<?> submit(Runnable task) {
        return instantExecutor.submit(monitorWrappedTask(task));
    }

    /**
     * Thực thi một tác vụ tức thì (ngay lập tức) dùng cho các logic nhạy thời gian.
     * <p>
     * Phù hợp cho:
     * - Xử lý gói tin từ client
     * - Tác vụ sự kiện người chơi (disconnect, tấn công, cast skill...)
     * - Thao tác real-time trong game loop
     * <p>
     * ❗ Lưu ý: Task cần chạy nhanh, không nên block I/O.
     * Nếu cần gọi DB hoặc xử lý file → dùng executeLongRunning().
     */
    @Override
    public void execute(Runnable command) {
        instantExecutor.execute(monitorWrappedTask(command));
    }

    private Runnable monitorWrappedTask(Runnable task) {
        return new RunnableWrapper(task, ThreadConfig.MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING, true);
    }

    public void shutdown() {
        log.info("Shutting down ThreadPoolManager...");

        scheduledExecutor.shutdown();
        if (instantExecutor instanceof ExecutorService executorService) {
            executorService.shutdown();
        }
        longRunningExecutor.shutdown();

        try {
            boolean finished = scheduledExecutor.awaitTermination(10, TimeUnit.SECONDS)
                    && (instantExecutor == null || instantExecutor.awaitTermination(10, TimeUnit.SECONDS))
                    && longRunningExecutor.awaitTermination(10, TimeUnit.SECONDS);
            log.info("Shutdown complete: {}", finished);
        } catch (InterruptedException e) {
            log.warn("Shutdown interrupted", e);
        }
    }

    public String getStats() {
        StringBuilder sb = new StringBuilder();

        sb.append("\nScheduled pool:\n");
        sb.append("=================================================\n");
        sb.append("getActiveCount: ...... ").append(scheduledExecutor.getActiveCount()).append("\n");
        sb.append("getCorePoolSize: ..... ").append(scheduledExecutor.getCorePoolSize()).append("\n");
        sb.append("getPoolSize: ......... ").append(scheduledExecutor.getPoolSize()).append("\n");
        sb.append("getLargestPoolSize: .. ").append(scheduledExecutor.getLargestPoolSize()).append("\n");
        sb.append("getMaximumPoolSize: .. ").append(scheduledExecutor.getMaximumPoolSize()).append("\n");
        sb.append("getCompletedTaskCount: ").append(scheduledExecutor.getCompletedTaskCount()).append("\n");
        sb.append("getQueuedTaskCount: .. ").append(scheduledExecutor.getQueue().size()).append("\n");
        sb.append("getTaskCount: ........ ").append(scheduledExecutor.getTaskCount()).append("\n\n");

        sb.append("Instant pool:\n");
        sb.append("=================================================\n");
        sb.append("getActiveCount: ...... ").append(instantExecutor.getActiveCount()).append("\n");
        sb.append("getCorePoolSize: ..... ").append(instantExecutor.getCorePoolSize()).append("\n");
        sb.append("getPoolSize: ......... ").append(instantExecutor.getPoolSize()).append("\n");
        sb.append("getLargestPoolSize: .. ").append(instantExecutor.getLargestPoolSize()).append("\n");
        sb.append("getMaximumPoolSize: .. ").append(instantExecutor.getMaximumPoolSize()).append("\n");
        sb.append("getCompletedTaskCount: ").append(instantExecutor.getCompletedTaskCount()).append("\n");
        sb.append("getQueuedTaskCount: .. ").append(instantExecutor.getQueue().size()).append("\n");
        sb.append("getTaskCount: ........ ").append(instantExecutor.getTaskCount()).append("\n\n");

        sb.append("Long running pool:\n");
        sb.append("=================================================\n");
        sb.append("getActiveCount: ...... ").append(longRunningExecutor.getActiveCount()).append("\n");
        sb.append("getCorePoolSize: ..... ").append(longRunningExecutor.getCorePoolSize()).append("\n");
        sb.append("getPoolSize: ......... ").append(longRunningExecutor.getPoolSize()).append("\n");
        sb.append("getLargestPoolSize: .. ").append(longRunningExecutor.getLargestPoolSize()).append("\n");
        sb.append("getMaximumPoolSize: .. ").append(longRunningExecutor.getMaximumPoolSize()).append("\n");
        sb.append("getCompletedTaskCount: ").append(longRunningExecutor.getCompletedTaskCount()).append("\n");
        sb.append("getQueuedTaskCount: .. ").append(longRunningExecutor.getQueue().size()).append("\n");
        sb.append("getTaskCount: ........ ").append(longRunningExecutor.getTaskCount()).append("\n");
        sb.append("=================================================\n");
        sb.append("TỔNG SỐ LUỒNG JVM ĐANG HOẠT ĐỘNG: ").append(Thread.activeCount()).append("\n");

        return sb.toString();
    }

    private static final class SingletonHolder {
        private static final ThreadPoolManager INSTANCE = new ThreadPoolManager();
    }

    public static ThreadPoolManager getInstance() {
        return SingletonHolder.INSTANCE;
    }
}

