package nro.utils.test.network;

import nro.commons.network.AConnection;
import nro.commons.network.Dispatcher;
import nro.commons.network.PacketProcessor;
import nro.commons.utils.concurrent.ExecuteWrapper;
import nro.server.config.main.ThreadConfig;
import nro.server.config.network.NetworkConfig;
import nro.server.config.network.PacketFloodFilterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public class TestConnection extends AConnection<ServerPacket> {

    private static final Logger log = LoggerFactory.getLogger(TestConnection.class);

    private static final PacketProcessor<TestConnection> packetProcessor = new PacketProcessor<>(NetworkConfig.PACKET_PROCESSOR_MIN_THREADS,
            NetworkConfig.PACKET_PROCESSOR_MAX_THREADS, NetworkConfig.PACKET_PROCESSOR_THREAD_SPAWN_THRESHOLD,
            NetworkConfig.PACKET_PROCESSOR_THREAD_KILL_THRESHOLD, new ExecuteWrapper(ThreadConfig.MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING));

    public enum State {
        CONNECTED,
        AUTHED,
        IN_GAME
    }

    private final Deque<ServerPacket> sendMsgQueue = new ArrayDeque<>();

    private volatile State state;

    private volatile long lastClientMessageTime;

    private Map<Integer, Long> pffRequests;
    private final ConnectionAliveChecker connectionAliveChecker;

    public TestConnection(SocketChannel sc, Dispatcher d) throws IOException {
        super(sc, d, 8192 * 4, 8192 * 4);

        System.out.println("new connection");

        state = State.CONNECTED;

        String ip = getIP();
        log.debug("connection from: " + ip);

        lastClientMessageTime = System.currentTimeMillis();
        connectionAliveChecker = new ConnectionAliveChecker();

        if (PacketFloodFilterConfig.PFF_MODE > 0 && PacketFloodFilterConfig.THRESHOLD_MILLIS_BY_PACKET_OPCODE != null)
            pffRequests = new ConcurrentHashMap<>();
    }


    @Override
    protected Queue<ServerPacket> getSendMsgQueue() {
        return null;
    }

    @Override
    protected boolean processData(ByteBuffer data) {
        return false;
    }

    @Override
    protected boolean writeData(ByteBuffer buffer) {
        return false;
    }

    @Override
    public void initialized() {

    }

    @Override
    protected void onDisconnect() {

    }

    @Override
    protected void onServerClose() {

    }

    private class ConnectionAliveChecker implements Runnable {

        private ScheduledFuture<?> task;

        private ConnectionAliveChecker() {
            if (connectionAliveChecker != null)
                throw new IllegalStateException("ConnectionAliveChecker for " + TestConnection.this + " is already assigned.");
//            task = ThreadPoolManager.getInstance().scheduleAtFixedRate(this, CM_PING.CLIENT_PING_INTERVAL, CM_PING.CLIENT_PING_INTERVAL);
        }

        private void stop() {
            task.cancel(false);
        }

        @Override
        public void run() {
            long millisSinceLastClientPacket = System.currentTimeMillis() - lastClientMessageTime;
            if (millisSinceLastClientPacket - 5000 > 180 * 1000) {
                log.info("Closing hanged up connection of " + TestConnection.this + " (last sign of life was " + millisSinceLastClientPacket + "ms ago)");
                close();
            }
        }
    }
}
