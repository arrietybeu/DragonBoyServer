package nro.server.services;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nro.commons.utils.concurrent.NamedRunnable;
import nro.server.utils.ThreadPoolManager;

/**
 * @author Arriety
 * @see <a href="https://github.com/arrietybeu">github.com/arrietybeu</a>
 */
public class PeriodicSaveService {

    private static final Logger log = LoggerFactory.getLogger(PeriodicSaveService.class);

    private final List<PeriodicSaveTask> tasks;

    private PeriodicSaveService() {
        tasks = Arrays.asList(new ShopKyGuiSaveTask());
    }

    public void onShutdown() {
        log.info("Starting data save on shutdown.");
        tasks.forEach(PeriodicSaveTask::storeDataAndCancel);
        log.info("Data successfully saved.");
    }

    private class ShopKyGuiSaveTask extends PeriodicSaveTask {

        private ShopKyGuiSaveTask() {
            super(TimeUnit.MINUTES.toMillis(2));// 2 phút
        }

        @Override
        public void run() {
            log.info("Saving shop ky gui data.");
        }
    }

    private abstract class PeriodicSaveTask implements Runnable {

        private final Future<?> future;

        private PeriodicSaveTask(long periodMillis) {
            future = ThreadPoolManager.getInstance().scheduleAtFixedRate(
                    new NamedRunnable(this.getClass().getSimpleName(), this), periodMillis, periodMillis);
        }

        /**
         * Hủy tác vụ định kỳ và thực hiện tác vụ lưu dữ liệu
         */
        private void storeDataAndCancel() {
            future.cancel(false);
            run();
        }
    }

    public static PeriodicSaveService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static final class SingletonHolder {
        private static final PeriodicSaveService INSTANCE = new PeriodicSaveService();
    }

}
