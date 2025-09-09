package nro.commons.utils.concurrent;

import nro.commons.configs.CommonsConfig;
import nro.commons.network.AConnection;
import nro.commons.network.packet.BaseClientPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class ExecuteWrapper implements Executor {

    private static final Logger log = LoggerFactory.getLogger(ExecuteWrapper.class);

    private final long expectedMaxExecutionTimeMillis;

    public ExecuteWrapper(long expectedMaxExecutionTimeMillis) {
        this.expectedMaxExecutionTimeMillis = expectedMaxExecutionTimeMillis;
    }

    @Override
    public void execute(Runnable runnable) {
        execute(runnable, expectedMaxExecutionTimeMillis, true);
    }

    public static void execute(Runnable runnable, long expectedMaxExecutionTimeMillis, boolean catchAndLogThrowables) {
        try {
            long begin = System.nanoTime();
            runnable.run();
            long durationNanos = System.nanoTime() - begin;

            if (CommonsConfig.RUNNABLESTATS_ENABLE)
                RunnableStatsManager.handleStats(runnable.getClass(), durationNanos);

            long durationMillis = TimeUnit.NANOSECONDS.toMillis(durationNanos);
            if (durationMillis > expectedMaxExecutionTimeMillis) {
                // String name = runnable.getClass().isAnonymousClass() ?
                // runnable.getClass().getName() : runnable.getClass().getSimpleName();
                String name;
                if (runnable instanceof BaseClientPacket<?> packet) {
                    name = packet.toFormattedPacketNameString();
                    try {
                        AConnection<?> c = packet.getConnection();
                        if (c != null) {
                            name = name + " from " + c.getIP();
                        }
                    } catch (Throwable ignored) {
                    }
                } else {
                    name = runnable.toString();
                }
                log.warn("{} - execution time: {}ms", name, durationMillis);
            }
        } catch (Throwable t) {
            if (catchAndLogThrowables)
                log.error("Exception in a Runnable execution:", t);
            else
                throw t;
        }
    }
}
