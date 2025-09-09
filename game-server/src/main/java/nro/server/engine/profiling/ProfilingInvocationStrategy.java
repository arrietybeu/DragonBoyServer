package nro.server.engine.profiling;

import com.artemis.BaseSystem;
import com.artemis.InvocationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public final class ProfilingInvocationStrategy extends InvocationStrategy {

    private static final Logger log = LoggerFactory.getLogger(ProfilingInvocationStrategy.class);

    private final long warnNs;

    public ProfilingInvocationStrategy(long warnMillis) {
        this.warnNs = TimeUnit.MILLISECONDS.toNanos(warnMillis);
    }

    @Override
    public void process() {
        BaseSystem[] arr = systems.getData();
        for (int i = 0, s = systems.size(); i < s; i++) {
            if (disabled.get(i))
                continue;

            updateEntityStates();

            BaseSystem sys = arr[i];
            long t0 = System.nanoTime();
            try {
                sys.process();
            } catch (Throwable t) {
                log.error("Exception in system {}", sys.getClass().getSimpleName(), t);
            } finally {
                long dt = System.nanoTime() - t0;
                if (dt > warnNs) {
                    long dtMs = TimeUnit.NANOSECONDS.toMillis(dt);
                    long thresholdMs = TimeUnit.NANOSECONDS.toMillis(warnNs);
                    log.warn("[SLOW] {} took {} ms (> {} ms)",
                            sys.getClass().getSimpleName(),
                            dtMs,
                            thresholdMs);
                }
            }
        }

        updateEntityStates();
    }
}
