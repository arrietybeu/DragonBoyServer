package nro.server.network.nro;

import lombok.Getter;
import nro.commons.network.AConnection;
import nro.commons.network.Dispatcher;
import nro.commons.utils.concurrent.ExecuteWrapper;
import nro.server.config.main.ThreadConfig;
import nro.server.config.network.NetworkConfig;
import nro.server.config.network.PacketFloodFilterConfig;
import nro.commons.network.PacketProcessor;
import nro.server.system.LogServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Getter
public class NroConnection extends AConnection<NroServerPacket> {

    private static final PacketProcessor<NroConnection> packetProcessor = new PacketProcessor<>(NetworkConfig.PACKET_PROCESSOR_MIN_THREADS,
            NetworkConfig.PACKET_PROCESSOR_MAX_THREADS, NetworkConfig.PACKET_PROCESSOR_THREAD_SPAWN_THRESHOLD,
            NetworkConfig.PACKET_PROCESSOR_THREAD_KILL_THRESHOLD, new ExecuteWrapper(ThreadConfig.MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING));

    public enum State {
        CONNECTED, AUTHED, IN_GAME
    }

    private final Deque<NroServerPacket> sendMsgQueue = new ArrayDeque<>();
    private Map<Integer, Long> pffRequests;

    private final State state;

    private final long lastClientMessageTime;

    private boolean encrypted;

    private final ConnectionAliveChecker connectionAliveChecker;

    public NroConnection(SocketChannel sc, Dispatcher d) throws IOException {
        super(sc, d, 8192 * 4, 8192 * 4);

        state = State.CONNECTED;

        String ip = getIP();

        LogServer.LogInfo("connection from: " + ip);

        lastClientMessageTime = System.currentTimeMillis();
        connectionAliveChecker = new ConnectionAliveChecker();

        if (PacketFloodFilterConfig.PFF_MODE > 0 && PacketFloodFilterConfig.THRESHOLD_MILLIS_BY_PACKET_OPCODE != null)
            pffRequests = new ConcurrentHashMap<>();
    }

    @Override
    protected Queue<NroServerPacket> getSendMsgQueue() {
        return sendMsgQueue;
    }

    @Override
    protected boolean processData(ByteBuffer data) {
        // TODO: Viết parser giải mã packet client gửi đến ở đây
        return true;
    }

    @Override
    protected boolean writeData(ByteBuffer buffer) {
        synchronized (guard) {
            if (sendMsgQueue.isEmpty()) return false;

            NroServerPacket packet = sendMsgQueue.removeFirst();
            long begin = System.nanoTime();
            packet.write(this, buffer);
            long duration = System.nanoTime() - begin;
            // TODO
            return true;
        }
    }

    @Override
    public void initialized() {
        // TODO Có thể gửi session key hoặc packet mở đầu ở đây nếu cần
    }

    @Override
    protected void onDisconnect() {
        // Xử lý logout, lưu player, cleanup resource
    }

    @Override
    protected void onServerClose() {
        close();
        safeLogout();
    }

    private void safeLogout() {
//        synchronized (this) {
//            Player player = getActivePlayer();
//            if (player == null) // player was already saved
//                return;
//            try {
//                PlayerLeaveWorldService.leaveWorld(player);
//            } catch (Exception e) {
//                log.error("Error saving " + player, e);
//            }
//        }
    }

    public boolean isBigPacket(int command) {
        return true;
    }

    public byte writeKey(int command) {
        return -1;
    }

    @Override
    public String toString() {
        return "NroConnection [state=" + state + ", ip=" + getIP() + "]";
    }

    private class ConnectionAliveChecker implements Runnable {

        private ScheduledFuture<?> task;

        private ConnectionAliveChecker() {
            if (connectionAliveChecker != null)
                throw new IllegalStateException("ConnectionAliveChecker for " + NroConnection.this + " is already assigned.");
//            task = ThreadPoolManager.getInstance().scheduleAtFixedRate(this, CM_PING.CLIENT_PING_INTERVAL, CM_PING.CLIENT_PING_INTERVAL);
        }

        private void stop() {
            task.cancel(false);
        }

        @Override
        public void run() {
            long millisSinceLastClientPacket = System.currentTimeMillis() - lastClientMessageTime;
//            if (millisSinceLastClientPacket - 5000 > CM_PING.CLIENT_PING_INTERVAL) {
//                LogServer.LogInfo("Closing hanged up connection of " + NroConnection.this + " (last sign of life was " + millisSinceLastClientPacket + "ms ago)");
//                close();
//            }
        }
    }

}
