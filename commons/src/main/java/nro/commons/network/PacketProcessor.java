package nro.commons.network;

import nro.commons.network.packet.BaseClientPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PacketProcessor<T extends AConnection<?>> {

    private static final Logger log = LoggerFactory.getLogger(PacketProcessor.class.getName());

    private final int threadSpawnThreshold;

    private final int threadKillThreshold;

    private final Lock lock = new ReentrantLock();

    private final Condition notEmpty = lock.newCondition();

    private final List<BaseClientPacket<T>> packets = new LinkedList<>();

    private final List<Thread> threads = new ArrayList<>();

    private final int minThreads;

    private final int maxThreads;

    private final Executor executor;

    public PacketProcessor(int minThreads, int maxThreads, int threadSpawnThreshold, int threadKillThreshold) {
        this(minThreads, maxThreads, threadSpawnThreshold, threadKillThreshold, new DummyExecutor());
    }

    public PacketProcessor(int minThreads, int maxThreads, int threadSpawnThreshold, int threadKillThreshold, Executor executor) {
        checkArgument(minThreads > 0, "Min Threads phải là số dương");
        checkArgument(maxThreads >= minThreads, "Số luồng tối đa phải >= Số luồng tối thiểu");
        checkArgument(threadSpawnThreshold > 0, "Thread Spawn Threshold must be positive");
        checkArgument(threadKillThreshold > 0, "Thread Kill Threshold must be positive");

        this.minThreads = minThreads;
        this.maxThreads = maxThreads;
        this.threadSpawnThreshold = threadSpawnThreshold;
        this.threadKillThreshold = threadKillThreshold;
        this.executor = executor;

        if (minThreads != maxThreads)
            startCheckerThread();

        for (int i = 0; i < minThreads; i++)
            newThread();
    }

    private void checkArgument(boolean condition, String errorMessage) {
        if (!condition)
            throw new IllegalArgumentException(errorMessage);
    }

    private void startCheckerThread() {
        Thread.ofVirtual().name("PacketProcessor:Checker").start(new CheckerTask());
    }

    private boolean newThread() {
        if (threads.size() >= maxThreads)
            return false;

        String name = "PacketProcessor:" + threads.size();
        log.debug("Creating new PacketProcessor Thread: " + name);

        Thread t = Thread.ofVirtual().name(name).unstarted(new PacketProcessorTask());
        threads.add(t);
        t.start();

        return true;
    }

    private void killThread() {
        if (threads.size() > minThreads) {
            Thread t = threads.removeLast();
            log.debug("Killing PacketProcessor Thread: " + t.getName());
            t.interrupt();
        }
    }

    public final void executePacket(BaseClientPacket<T> packet) {
        lock.lock();
        try {
            packets.add(packet);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    private BaseClientPacket<T> getFirstAvailable() {
        for (; ; ) {
            while (packets.isEmpty())
                notEmpty.awaitUninterruptibly();

            ListIterator<BaseClientPacket<T>> it = packets.listIterator();
            while (it.hasNext()) {
                BaseClientPacket<T> packet = it.next();
                if (packet.getConnection().tryLockConnection()) {
                    it.remove();
                    return packet;
                }
            }
            notEmpty.awaitUninterruptibly();
        }
    }

    private static class DummyExecutor implements Executor {

        @Override
        public void execute(Runnable command) {
            command.run();
        }

    }

    private final class CheckerTask implements Runnable {

        private static final Duration CHECK_INTERVAL = Duration.ofMinutes(1);
        private int previousPacketCount = 0;

        @Override
        public void run() {
            for (; ; ) {
                try {
                    Thread.sleep(CHECK_INTERVAL);
                } catch (InterruptedException e) {
                    return;
                }

                int packetsWaitingForExecution = packets.size();
                if (packetsWaitingForExecution <= previousPacketCount && packetsWaitingForExecution <= threadKillThreshold) {
                    // reduce thread count by one
                    killThread();
                } else if (packetsWaitingForExecution > threadSpawnThreshold) {
                    // too small amount of threads
                    if (!newThread() && packetsWaitingForExecution >= threadSpawnThreshold * 3)
                        log.warn("Lag detected! [" + packetsWaitingForExecution
                                + " client packets are waiting for execution]. You should consider increasing PacketProcessor maxThreads or hardware upgrade.");
                }
                previousPacketCount = packetsWaitingForExecution;
            }
        }
    }

    private final class PacketProcessorTask implements Runnable {

        @Override
        public void run() {
            BaseClientPacket<T> packet = null;
            for (; ; ) {
                lock.lock();
                try {
                    if (packet != null)
                        packet.getConnection().unlockConnection();

                    /* thread killed */
                    if (Thread.interrupted())
                        return;

                    packet = getFirstAvailable();
                } finally {
                    lock.unlock();
                }
                executor.execute(packet);
            }
        }
    }


}
