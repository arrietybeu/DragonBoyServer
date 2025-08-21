package nro.server.network.nro;

import com.artemis.Entity;
import com.artemis.World;
import lombok.Getter;
import lombok.Setter;
import nro.commons.configs.CommonsConfig;
import nro.commons.consts.ConstsCmd;
import nro.commons.network.AConnection;
import nro.commons.network.Crypt;
import nro.commons.network.Dispatcher;
import nro.commons.network.PacketProcessor;
import nro.commons.utils.concurrent.ExecuteWrapper;
import nro.commons.utils.concurrent.RunnableStatsManager;
import nro.server.GameServer;
import nro.server.configs.main.ThreadConfig;
import nro.server.configs.network.NetworkConfig;
import nro.server.model.account.Account;
import nro.server.model.session.SessionInfo;
import nro.server.network.nro.client_packets.NroClientPacketFactory;
import nro.server.network.nro.server_packets.handler.SMSendKey;
import nro.server.network.nro.server_packets.handler.SmDialogMessage;
import nro.server.services.player.PlayerLeaveWorldService;
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
    @Setter
    private Account account;

    @Getter
    private final Crypt crypt;

    @Getter
    private volatile Entity entity;

    @Getter
    @Setter
    private volatile int playerID = -1;

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

    public Account getAccount() {
        if (account == null) {
            throw new IllegalStateException("Account is not set for connection: " + this);
        }
        return account;
    }

    /**
     * Gán một Entity Player vào Connection này sau khi đăng nhập thành công.
     * <p>Cập nhật trạng thái của connection thành IN_GAME.</p>
     *
     * @param entityId ID của entity người chơi
     */
    public void attachPlayerEntity(Entity entityId) {
        this.entity = entityId;
        setState(State.IN_GAME);
    }

    /**
     * Tách Entity ra khỏi Connection, thường dùng khi đăng xuất.
     */
    public void detachPlayerEntity() {
        this.entity = null;
        setState(State.AUTHED);
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
    public boolean processData(ByteBuffer rb) throws RuntimeException {
        final boolean isEncrypted = getCrypt().isSendKey();

        int startPos = rb.position();

        byte cmd = rb.get();
        if (!ConstsCmd.IGNORE_CMD.contains(cmd)) {
            System.out.println("Received command: " + cmd);
        }
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
                return false;
            }
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
                var msg = "Error processing packet write: " + packet.getClass().getSimpleName() + " for ID: " + playerID;
                log.error("Error processing packet write: [{}] for ID:", packet.getClass().getSimpleName(), e);
                close(new SmDialogMessage(msg));
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

        var player = getEntity();
        if (player != null) {

            PlayerLeaveWorldService.leaveWorld(this);
        }

        log.info("Client disconnected successfully: IP={}, state={}", getIP(), state + " time delay" + pendingCloseUntilMillis);
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
        synchronized (this) {
            Entity player = getEntity();
            if (player == null) // player was already saved
                return;
            try {
                PlayerLeaveWorldService.leaveWorld(this);
            } catch (Exception e) {
                log.error("Error saving player id {}", this.playerID, e);
            }
        }
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
