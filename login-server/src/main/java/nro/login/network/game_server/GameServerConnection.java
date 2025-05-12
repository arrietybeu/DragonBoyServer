package nro.login.network.game_server;

import lombok.Getter;
import nro.commons.network.AConnection;
import nro.commons.network.Crypt;
import nro.commons.network.Dispatcher;
import nro.login.GameServerInfo;
import nro.login.PingPongTask;
import nro.login.network.factories.GsPacketHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@Getter
public class GameServerConnection extends AConnection<GameServerPacket> {

    private static final Logger log = LoggerFactory.getLogger(GameServerConnection.class);
    private static final ExecutorService PACKET_EXECUTOR = Executors.newCachedThreadPool();
    private static final ScheduledExecutorService PINGPONG_EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    static {
        ((ThreadPoolExecutor) PACKET_EXECUTOR).setCorePoolSize(1);
    }

    public enum State {
        /**
         * Means that GameServer just connect, but is not authenticated yet
         */
        CONNECTED,
        /**
         * GameServer is authenticated
         */
        AUTHED
    }

    private final Deque<GameServerPacket> sendMsgQueue = new ArrayDeque<>();

    @Getter
    private State state;

    private GameServerInfo gameServerInfo = null;

    private final PingPongTask pingPongTask = new PingPongTask(this);

    public GameServerConnection(SocketChannel sc, Dispatcher d) throws IOException {
        super(sc, d, 8192 * 8, 8192 * 8);
    }

    public void setState(State state) {
        this.state = state;
        if (state == State.AUTHED) {
            pingPongTask.start(PINGPONG_EXECUTOR);
        }
    }

    @Override
    public void initialized() {
        state = State.CONNECTED;
        log.info("GAME_SERVER connection attempt from: {}", getIP());
    }

    @Override
    protected final Queue<GameServerPacket> getSendMsgQueue() {
        return sendMsgQueue;
    }

    @Override
    public boolean processData(ByteBuffer data) {
        GameServerClientPacket pck = GsPacketHandlerFactory.handle(data, this);

        if (pck != null && pck.read())
            PACKET_EXECUTOR.execute(pck);

        return true;
    }

    @Override
    protected final boolean writeData(ByteBuffer data) {
        synchronized (guard) {
            GameServerPacket packet = sendMsgQueue.pollFirst();
            if (packet == null)
                return false;

            packet.write(this, data);
            return true;
        }
    }

    @Override
    protected final void onDisconnect() {
        pingPongTask.stop();
        log.info("{} disconnected", this);
        if (gameServerInfo != null) {
            gameServerInfo.setConnection(null);
            gameServerInfo.getAccountsOnGameServer().clear();
            gameServerInfo = null;
        }
//        AccountController.updateServerListForAllLoggedInPlayers();
    }

    @Override
    protected final void onServerClose() {
        close();
        PINGPONG_EXECUTOR.shutdown();
        PACKET_EXECUTOR.shutdown();
    }

    @Override
    protected Crypt getCrypt() {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GAME_SERVER");
        if (gameServerInfo != null)
            sb.append(" #").append(gameServerInfo.getId());
        sb.append(" ").append(getIP());
        return sb.toString();
    }

}
