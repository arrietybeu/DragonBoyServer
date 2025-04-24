package nro.commons.utils.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class PriorityThreadFactory implements ThreadFactory {

    /**
     * Priority of new threads
     */
    private final int prio;
    /**
     * Thread group name
     */
    private final String threadName;
    /**
     * Number of created threads
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    /**
     * ThreadGroup for created threads
     */
    private final ThreadGroup group;

    /**
     * Constructor.
     *
     * @param threadName
     * @param prio
     */
    public PriorityThreadFactory(String threadName, int prio) {
        this.prio = prio;
        this.threadName = threadName;
        this.group = new ThreadGroup(this.threadName);
    }

    @Override
    public Thread newThread(final Runnable r) {
        Thread t = new Thread(group, r);
        t.setName(threadName + "-" + threadNumber.getAndIncrement());
        t.setPriority(prio);
        return t;
    }
}
