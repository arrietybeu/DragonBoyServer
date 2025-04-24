package nro.server.network;

import lombok.Getter;
import nro.commons.network.AConnection;
import nro.commons.network.Dispatcher;
import nro.server.network.nro.NroServerPacket;
import nro.server.utils.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.ScheduledFuture;

public class NroConnection extends AConnection<NroServerPacket> {

    private static final Logger log = LoggerFactory.getLogger(NroConnection.class);

    private static final int READ_BUFFER_SIZE = 8192;
    private static final int WRITE_BUFFER_SIZE = 8192;

    @Getter
    private volatile State state;

    private long lastMessageTimestamp = 0;
    private int messageCount = 0;
    private static final int MAX_MESSAGES_PER_INTERVAL = 30; // Ví dụ: Lấy từ ConfigServer.MAX_MESSAGES_PER_5_SECONDS
    private static final long MESSAGE_INTERVAL = 5000; // 5 giây

    private volatile long lastClientMessageTime;

    private final ConnectionAliveChecker connectionAliveChecker;
    private final Deque<NroServerPacket> sendMsgQueue = new ArrayDeque<>();

    public enum State {
        /**
         * mới kết nối, chờ gửi key
         */
        CONNECTED,
        /**
         * Đã xác thực key, chờ đăng nhập/chọn nhân vật
         */
        AUTHED,
        /**
         * Đã vào game
         */
        IN_GAME
    }

    public NroConnection(SocketChannel sc, Dispatcher d) throws IOException {
        super(sc, d, READ_BUFFER_SIZE * 4, WRITE_BUFFER_SIZE * 4);
        this.state = State.CONNECTED;

        String ip = getIP();

        this.lastMessageTimestamp = System.currentTimeMillis();
        connectionAliveChecker = new ConnectionAliveChecker();

        log.debug("Connection established: {}", ip);
//        if (PffConfig.PFF_MODE > 0 && PffConfig.THRESHOLD_MILLIS_BY_PACKET_OPCODE != null)
//            pffRequests = new ConcurrentHashMap<>();
//        SessionManager.getInstance().add(this);
    }

    /**
     * Trả về hàng đợi ArrayDeque
     * Lưu ý: Kiểu trả về là Queue, ArrayDeque implements Deque, Deque extends Queue
     *
     * @return Deque
     */
    @Override
    protected Queue<NroServerPacket> getSendMsgQueue() {
        return this.sendMsgQueue;
    }

    @Override
    protected boolean processData(ByteBuffer data) {
        return false;
    }

    @Override
    protected boolean writeData(ByteBuffer buffer) {
        NroServerPacket packet;
        synchronized (guard) {
            packet = sendMsgQueue.poll();
        }

        if (packet == null) return false; // Hết packet để gửi

        try {
            packet.write(this, buffer);
        } catch (Exception e) {
            log.error("Error processing packet write {} for ID:", packet.getClass().getSimpleName(), e);
            close();
            return false;
        }
        return true;
    }

    @Override
    public void initialized() {
        log.debug("Connection initialized for ID: ");
    }

    @Override
    protected void onDisconnect() {
        connectionAliveChecker.stop();

//        if (GameServer.isShuttingDownSoon()) { // client crashing during last seconds of countdown
//            safeLogout(); // instant synchronized leaveWorld to ensure completion before onServerClose
//            return;
//        }
        // Dọn dẹp hàng đợi gửi (nên làm trong synchronized để an toàn)
        synchronized (guard) {
            if (sendMsgQueue != null) {
                sendMsgQueue.clear();
            }
        }
        log.info("Client disconnected successfully: ID=");
    }

    /**
     * Sửa dụng khi server shutdown
     * <code>close()</code> server close connection client
     * <code>safeLogout();</code> save data player
     */
    @Override
    protected void onServerClose() {
        log.warn("Server closing, force disconnect: ID=");
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

    private class ConnectionAliveChecker implements Runnable {

        private final ScheduledFuture<?> task;

        private ConnectionAliveChecker() {
            if (connectionAliveChecker != null)
                throw new IllegalStateException("ConnectionAliveChecker for " + NroConnection.this + " is already assigned.");
            task = ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 180 * 1000, 180 * 1000);
        }

        private void stop() {
            task.cancel(false);
        }

        @Override
        public void run() {
            long millisSinceLastClientPacket = System.currentTimeMillis() - lastClientMessageTime;
            if (millisSinceLastClientPacket - 5000 > 180 * 1000) {
                log.info("Closing hanged up connection of {} (last sign of life was {}ms ago)", NroConnection.this, millisSinceLastClientPacket);
                close();
            }
        }
    }

}
