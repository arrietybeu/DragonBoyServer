package nro.login;

import nro.login.network.game_server.GameServerConnection;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PingPongTask implements Runnable {

    private final GameServerConnection connection;
    private final AtomicInteger unrespondedPingCount = new AtomicInteger();
    private Future<?> task;

    public PingPongTask(GameServerConnection connection) {
        this.connection = connection;
    }

    public void start(ScheduledExecutorService scheduledExecutorService) {
        if (task != null)
            throw new UnsupportedOperationException("PingPongTask was already started");
        task = scheduledExecutorService.scheduleAtFixedRate(this, 5, 5, TimeUnit.SECONDS);
    }

    public void stop() {
        if (task != null)
            task.cancel(false);
    }

    public void onReceivePong() {
        unrespondedPingCount.set(0);
    }


    @Override
    public void run() {
        if (unrespondedPingCount.getAndIncrement() <= 2) {
            connection.sendPacket(new SM_PING());
        } else {
            stop();
            LoggerFactory.getLogger(PingPongTask.class).warn("Gameserver #{} connection died, closing it.", connection.getGameServerInfo().getId());
            connection.close();
        }
    }
}
