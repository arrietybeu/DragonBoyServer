package nro.server.network.nro;

import lombok.Getter;
import lombok.Setter;
import nro.commons.configs.CommonsConfig;
import nro.commons.network.AConnection;
import nro.commons.network.Crypt;
import nro.commons.network.Dispatcher;
import nro.commons.network.PacketProcessor;
import nro.commons.utils.concurrent.ExecuteWrapper;
import nro.commons.utils.concurrent.RunnableStatsManager;
import nro.server.GameServer;
import nro.server.configs.main.ThreadConfig;
import nro.server.configs.network.NetworkConfig;
import nro.server.model.entity.player.Player;
import nro.server.model.template.session.SessionInfo;
import nro.server.network.nro.client_packets.NroClientPacketFactory;
import nro.server.network.nro.server_packets.handler.SMSendKey;
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
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Arriety Bếu
 */
public class NroConnection extends AConnection<NroServerPacket> {

    private static final Logger log = LoggerFactory.getLogger(NroConnection.class);

    private static final PacketProcessor<NroConnection> packetProcessor = new PacketProcessor<>(
            NetworkConfig.PACKET_PROCESSOR_MIN_THREADS,
            NetworkConfig.PACKET_PROCESSOR_MAX_THREADS,
            NetworkConfig.PACKET_PROCESSOR_THREAD_SPAWN_THRESHOLD,
            NetworkConfig.PACKET_PROCESSOR_THREAD_KILL_THRESHOLD,
            new ExecuteWrapper(ThreadConfig.MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING)
    );

    @Setter
    @Getter
    private volatile State state;
    @Getter
    private final SessionInfo sessionInfo;
    @Getter
    private final Crypt crypt;

    private final AtomicReference<Player> activePlayer = new AtomicReference<>();

    private volatile long lastClientMessageTime;

    private final ConnectionAliveChecker connectionAliveChecker;
    private final Deque<NroServerPacket> sendMsgQueue = new ArrayDeque<>();

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
        this.sessionInfo = new SessionInfo();
        this.crypt = new Crypt();
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
    public boolean processData(ByteBuffer rb) {
        final boolean isEncrypted = getCrypt().isSendKey();

        int startPos = rb.position();

        byte cmd = rb.get();
        byte b1 = rb.get();
        byte b2 = rb.get();

        if (isEncrypted) {
            cmd = getCrypt().decryptByte(cmd);
        }

        int bodySize = ((b1 & 0xFF) << 8) | (b2 & 0xFF);

        if (rb.remaining() < bodySize) {
            log.warn("Not enough bytes for full payload. cmd={}, expect bodySize={}, available={}", cmd, bodySize, rb.remaining());
            rb.position(startPos);
            return true;
        }

        byte[] payload = new byte[bodySize];
        rb.get(payload);

        if (isEncrypted) {
            for (int i = 0; i < payload.length; i++) {
                payload[i] = getCrypt().decryptByte(payload[i]);
            }
        }

        ByteBuffer packet = ByteBuffer.allocate(1 + 2 + payload.length);
        packet.put(cmd);
        packet.put(b1);
        packet.put(b2);
        packet.put(payload);
        packet.flip();

        ByteBuffer bodyBuf = ByteBuffer.wrap(payload);

        NroClientPacket p = NroClientPacketFactory.createPacket(cmd, bodyBuf, this);

        if (p != null) {
            if (p.read()) {
                packetProcessor.executePacket(p);
            } else {
                log.warn("Invalid packet cmd={} in state={}", cmd, state);
            }
        } else {
            log.warn("Unknown packet cmd={} in state={}", cmd, state);
        }
        return true;
    }

    @Override
    protected boolean writeData(ByteBuffer buffer) {
        NroServerPacket packet;
        synchronized (guard) {
            packet = sendMsgQueue.poll();

            if (packet == null) return false; // het packet de gui

            long begin = System.nanoTime();

            try {
                packet.write(this, buffer);
            } catch (Exception e) {
                log.error("Error processing packet write: [{}] for ID:", packet.getClass().getSimpleName(), e);
                close();
                return false;
            } finally {
                if (CommonsConfig.RUNNABLESTATS_ENABLE) {
                    long duration = System.nanoTime() - begin;
                    RunnableStatsManager.handleStats(packet.getClass(), "runImpl()", duration);
                }
                if (buffer.limit() > NroServerPacket.MAX_USABLE_PACKET_BODY_SIZE)
                    log.warn("{} contains {} more bytes than the game client of {} can read", packet, buffer.limit() - NroServerPacket.MAX_USABLE_PACKET_BODY_SIZE, null);
            }
            return true;
        }
    }

    @Override
    public void initialized() {
        sendPacket(new SMSendKey());
    }

    public final void encrypt() {
        this.getCrypt().encrypt();
    }

    /**
     * Sets Active player to new value. Update connection state to correct value.
     *
     * @param player
     * @return True if active player was set to new value.
     */
    public boolean setActivePlayer(Player player) {
        if (player == null) {
            activePlayer.set(null);
            setState(State.AUTHED);
        } else if (activePlayer.compareAndSet(null, player)) {
            setState(State.IN_GAME);
        } else {
            return false;
        }
        return true;
    }

    @Override
    protected void onDisconnect() {
        connectionAliveChecker.stop();

        if (GameServer.isShuttingDownSoon()) { // client crashing during last seconds of countdown
            safeLogout(); // instant synchronized leaveWorld to ensure completion before onServerClose
            return;
        }

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

    @Override
    public String toString() {
        return "NroConnection [state=" + state + "], getIP()=" + getIP() + "]";
    }

}
