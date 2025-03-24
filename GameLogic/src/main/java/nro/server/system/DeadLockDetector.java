package nro.server.system;

import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.time.Duration;

public class DeadLockDetector extends Thread {

    private final Duration _checkInterval;
    private final Runnable _deadLockCallback;
    private final ThreadMXBean tmx;

    public DeadLockDetector(Duration checkInterval, Runnable deadLockCallback) {
        super("DeadLockDetector");
        this._checkInterval = checkInterval;
        this._deadLockCallback = deadLockCallback;
        this.tmx = ManagementFactory.getThreadMXBean();
        this.setDaemon(true);
    }

    @Override
    public final void run() {
        boolean deadlock = false;
        while (!deadlock) {
            try {
                long[] ids = this.tmx.findDeadlockedThreads();
                if (ids != null) {
                    deadlock = true;
                    ThreadInfo[] tis = this.tmx.getThreadInfo(ids, true, true);
                    StringBuilder info = new StringBuilder();
                    info.append("DeadLock Found!\n");
                    for (ThreadInfo ti : tis) {
                        info.append(ti.toString());
                    }
                    for (ThreadInfo ti : tis) {
                        LockInfo[] locks = ti.getLockedSynchronizers();
                        MonitorInfo[] monitors = ti.getLockedMonitors();
                        if (locks.length == 0 && monitors.length == 0) {
                            continue;
                        }
                        ThreadInfo dl = ti;
                        info.append("Java-level deadlock:\n");
                        do {
                            Method(info, dl);
                        } while ((dl = this.tmx.getThreadInfo(new long[]{dl.getLockOwnerId()}, true, true)[0]).getThreadId() != ti.getThreadId());
                    }
                    LogServer.LogWarning(info.toString());
                    if (this._deadLockCallback != null) {
                        this._deadLockCallback.run();
                    }
                }
                Thread.sleep(this._checkInterval.toMillis());
            } catch (Exception e) {
                LogServer.LogWarning("DeadLockDetector: " + e.getMessage());
            }
        }
    }

    private void Method(StringBuilder info, ThreadInfo dl) {
        info.append('\t');
        info.append(dl.getThreadName());
        info.append(" is waiting to lock ");
        info.append(dl.getLockInfo().toString());
        info.append(" which is held by ");
        info.append(dl.getLockOwnerName());
        info.append("\n");
    }
}
