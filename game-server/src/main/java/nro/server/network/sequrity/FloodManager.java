package nro.server.network.sequrity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author Arriety
 */
public final class FloodManager {

    public record FloodFilter(int warnLimit, int rejectLimit, int tickLimit) {
    }

    private final class LogEntry {

        private final short[] ticks = new short[tickAmount];

        private int lastTick = getCurrentTick();

        public int getCurrentTick() {
            return (int) ((System.currentTimeMillis() - ZERO) / tickLength);
        }

        public boolean isActive() {
            return getCurrentTick() - lastTick < tickAmount * 10;
        }

        public Result isFlooding(final boolean increment) {
            final int currentTick = getCurrentTick();

            if (currentTick - lastTick >= ticks.length) {
                lastTick = currentTick;
                Arrays.fill(ticks, (short) 0);
            } else if (lastTick > currentTick) {
                log.warn("The current tick ({}) is smaller than the last ({})!", currentTick, lastTick, new IllegalStateException());
                lastTick = currentTick;
            } else
                while (currentTick != lastTick) {
                    lastTick++;
                    ticks[lastTick % ticks.length] = 0;
                }

            if (increment)
                ticks[lastTick % ticks.length]++;

            for (FloodFilter filter : filters) {
                int previousSum = 0;
                int currentSum = 0;

                for (int i = 0; i <= filter.tickLimit(); i++) {
                    int value = ticks[(lastTick - i) % ticks.length];

                    if (i != 0)
                        previousSum += value;

                    if (i != filter.tickLimit())
                        currentSum += value;
                }

                if (previousSum > filter.rejectLimit() || currentSum > filter.rejectLimit())
                    return Result.REJECTED;

                if (previousSum > filter.warnLimit() || currentSum > filter.warnLimit())
                    return Result.WARNED;
            }

            return Result.ACCEPTED;
        }
    }

    public enum Result {
        ACCEPTED,
        WARNED,
        REJECTED;

        public static Result max(final Result r1, final Result r2) {
            if (r1.ordinal() > r2.ordinal())
                return r1;

            return r2;
        }
    }

    public final Logger log = LoggerFactory.getLogger(FloodManager.class);

    private static final long ZERO = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1);

    private final Map<String, LogEntry> entries = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    private final int tickLength;

    private final int tickAmount;

    private final FloodFilter[] filters;

    public FloodManager(final int msecPerTick, final FloodFilter... filters) {
        tickLength = msecPerTick;
        this.filters = filters;

        int max = 1;

        for (FloodFilter filter : this.filters)
            max = Math.max(filter.tickLimit() + 1, max);

        tickAmount = max;

        NetFlusher.add(this::flush, 60000);
    }

    private void flush() {
        lock.lock();
        try {
            for (Iterator<LogEntry> it = entries.values().iterator(); it.hasNext(); ) {
                if (it.next().isActive())
                    continue;

                it.remove();
            }
        } finally {
            lock.unlock();
        }
    }

    public Result isFlooding(final String key, final boolean increment) {
        if (key == null || key.isEmpty())
            return Result.REJECTED;

        lock.lock();
        try {
            LogEntry entry = entries.get(key);

            if (entry == null) {
                entry = new LogEntry();

                entries.put(key, entry);
            }

            return entry.isFlooding(increment);
        } finally {
            lock.unlock();
        }
    }
}
