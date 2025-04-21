package nro.server.network.nro;

import lombok.Getter;
import nro.commons.network.AConnection;
import nro.commons.network.Dispatcher;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

@Getter
public class NroConnection extends AConnection<NroServerPacket> {

    public enum State {
        CONNECTED, AUTHED, IN_GAME
    }

    private final Deque<NroServerPacket> sendMsgQueue = new ArrayDeque<>();

    private volatile State state;

    private boolean encrypted;

    public NroConnection(SocketChannel sc, Dispatcher d, int rbSize, int wbSize) throws IOException {
        super(sc, d, rbSize, wbSize);
        this.state = State.CONNECTED;
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
            // Nếu bạn dùng thống kê thời gian có thể log ra đây

            return true;
        }
    }

    @Override
    protected void initialized() {
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

    @Override
    public String toString() {
        return "NroConnection [state=" + state + ", ip=" + getIP() + "]";
    }
}
