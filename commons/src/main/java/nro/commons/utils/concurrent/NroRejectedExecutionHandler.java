package nro.commons.utils.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public final class NroRejectedExecutionHandler implements RejectedExecutionHandler {

    private static final Logger log = LoggerFactory.getLogger(NroRejectedExecutionHandler.class);

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (executor.isShutdown())
            return;

        log.warn(r + " from " + executor, new RejectedExecutionException());

        if (Thread.currentThread().getPriority() > Thread.NORM_PRIORITY)
            new Thread(r).start();
        else
            r.run();
    }
}
