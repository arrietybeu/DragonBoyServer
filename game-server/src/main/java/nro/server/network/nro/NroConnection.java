package nro.server.network.nro;

import lombok.Getter;
import nro.commons.network.AConnection;
import nro.commons.network.Dispatcher;
import nro.commons.network.PacketProcessor;
import nro.commons.utils.concurrent.ExecuteWrapper;
import nro.server.configs.main.ThreadConfig;
import nro.server.configs.network.NetworkConfig;
import nro.server.network.nro.client_packets.NroClientPacketFactory;
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

    private static final PacketProcessor<NroConnection> packetProcessor = new PacketProcessor<>(
            NetworkConfig.PACKET_PROCESSOR_MIN_THREADS,
            NetworkConfig.PACKET_PROCESSOR_MAX_THREADS,
            NetworkConfig.PACKET_PROCESSOR_THREAD_SPAWN_THRESHOLD,
            NetworkConfig.PACKET_PROCESSOR_THREAD_KILL_THRESHOLD,
            new ExecuteWrapper(ThreadConfig.MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING)
    );

    @Getter
    private volatile State state;
    private final NroCrypt crypt = new NroCrypt();
    private final ConnectionAliveChecker connectionAliveChecker;
    private final Deque<NroServerPacket> sendMsgQueue = new ArrayDeque<>();

    private volatile long lastClientMessageTime;

    public enum State {
        /**
         * các message thực thi ở khi client connection
         */
        CONNECTED,
        /**
         * Đã xác thực key, chờ đăng nhập/chọn nhân vật
         */
        AUTHED,
        /**
         * các state message chỉ dùng khi đã vào game
         */
        IN_GAME
    }

    public NroConnection(SocketChannel sc, Dispatcher d) throws IOException {
        super(sc, d, NetworkConfig.READ_BUFFER_SIZE, NetworkConfig.WRITE_BUFFER_SIZE);
        this.state = State.CONNECTED;
        String ip = getIP();
        connectionAliveChecker = new ConnectionAliveChecker();
        log.debug("Connection established: {}", ip);
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
    public boolean processData(ByteBuffer data) {
        if (!crypt.isEnabled()) return true;

        if (!crypt.decrypt(data)) {
            return false;
        }

        byte cmd = data.get();
        ByteBuffer sliced = data.slice();

        NroClientPacket packet = NroClientPacketFactory.createPacket(ByteBuffer.wrap(new byte[]{cmd}).put(sliced).flip(), this);

        if (packet != null && packet.read()) {
//            ThreadPoolManager.getInstance().execute(packet);
            packetProcessor.executePacket(packet);
        }
        return true;
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
            if (sendMsgQueue != null && !sendMsgQueue.isEmpty()) {
                log.info("clear sendMsgQueue for onDisconnect size: {}", sendMsgQueue.size());
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
