package nro.server.network.sequrity;

import java.util.Timer;
import java.util.TimerTask;

public final class NetFlusher {

    private static final Timer _timer = new Timer(NetFlusher.class.getName(), true);

    public static void add(final Runnable runnable, long interval) {
        _timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }, interval, interval);
    }
}
