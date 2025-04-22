package nro.commons.network;


import lombok.Getter;
import nro.commons.network.packet.BaseServerPacket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Lớp này đại diện cho một kết nối với socket server. Kết nối được tạo ra bởi <code>ConnectionFactory</code> và gán với
 * <code>SelectionKey</code> này sẽ được đăng ký vào một Dispatcher (luồng xử lý) để xử lý các thao tác đọc/ghi bằng NIO.
 */

public abstract class AConnection<T extends BaseServerPacket> {

    /**
     * Đây là socket dùng để giao tiếp TCP giữa client và server.
     * Chính là kênh để đọc/ghi dữ liệu NIO non-blocking.
     * -- GETTER --
     * Trả về <code>SocketChannel</code> hiện tại
     */
    @Getter
    private final SocketChannel socketChannel;

    /**
     * Là đối tượng <code>Dispatcher</code> (thread xử lý IO: đọc/ghi) mà kết nối này được gán vào.
     * Tức là mỗi <code>Dispatcher</code> sẽ quản lý nhiều kết nối <code>AConnection</code> thông qua selector.
     */

    private final Dispatcher dispatcher;

    /**
     * Đây là key đại diện cho kết nối này trong <code>Selector</code>.
     * Key này giữ thông tin đăng ký đọc/ghi cho socket.
     */
    private SelectionKey key;

    /**
     * Thời điểm đóng kết nối thật sự.
     * Nếu gọi <code>close()</code> có gửi thêm <code>closePacket</code>, thì sẽ đợi 2 giây mới đóng — lúc đó sẽ set <code>pendingCloseUntilMillis = now + 2000</code>.
     */
    protected long pendingCloseUntilMillis;

    /**
     * Cờ đánh dấu kết nối đã bị đóng chưa.
     * Để tránh gọi <code>close()</code> nhiều lần.
     */
    protected boolean closed;

    /**
     * <code>Object</code> khoá (lock) dùng để đồng bộ hoá nhiều thao tác nguy hiểm như gửi dữ liệu, đóng kết nối.
     */
    protected final Object guard = new Object();

    /**
     * <code>ByteBuffer</code> dùng để đọc và ghi dữ liệu NIO.
     * Đây là nơi <code>Dispatcher</code> sẽ đổ byte từ channel vào và xử lý tiếp.
     * <p>
     * writeBuffer: ghi dữ liệu từ server về client.
     * <p>
     * readBuffer: chứa dữ liệu client gửi lên server, đang chờ xử lý.
     */
    public final ByteBuffer writeBuffer;
    public final ByteBuffer readBuffer;

    /**
     * Địa chỉ IP của client này.
     * Được cache lại khi tạo <code>AConnection</code>, để dùng sau khi socket đóng vẫn còn biết IP.
     */
    private final String ip;

    /**
     * Được dùng khi <code>dispatcher</code> đang xử lý packet từ buffer, để tránh 2 thread cùng xử lý 1 connection cùng lúc.
     * Giống kiểu mutex.
     */
    private boolean locked = false;

    /**
     * Constructor
     *
     * @param sc
     * @param d
     * @throws IOException
     */
    public AConnection(SocketChannel sc, Dispatcher d, int rbSize, int wbSize) throws IOException {
        socketChannel = sc;
        dispatcher = d;
        writeBuffer = ByteBuffer.allocate(wbSize);
        writeBuffer.flip();
        writeBuffer.order(ByteOrder.LITTLE_ENDIAN);
        readBuffer = ByteBuffer.allocate(rbSize);
        readBuffer.order(ByteOrder.LITTLE_ENDIAN);

        this.ip = socketChannel.socket().getInetAddress().getHostAddress();
    }

    /**
     * Gán key khi <code>socketChannel</code> được đăng ký vào <code>Dispatcher</code>
     */
    final void setKey(SelectionKey key) {
        this.key = key;
    }

    /**
     * Gửi 1 packet tới client nếu đang kết nối
     */
    public final void sendPacket(T serverPacket) {
        synchronized (guard) {
            if (pendingCloseUntilMillis != 0 || closed)
                return;

            if (isConnected()) {
                getSendMsgQueue().add(serverPacket);
                key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                key.selector().wakeup(); // bé ơi ngủ dậy đi
            } else {
                close();
            }
        }
    }

    /**
     * Đóng kết nối không gửi packet
     */
    public final void close() {
        close(null);
    }

    /**
     * Đóng kết nối và có thể gửi 1 packet cuối cùng
     */
    public final void close(T closePacket) {
        synchronized (guard) {
            if (pendingCloseUntilMillis != 0 || closed)
                return;

            pendingCloseUntilMillis = System.currentTimeMillis() + 2000;

            if (closePacket != null || !isConnected())
                getSendMsgQueue().clear();

            if (closePacket != null && isConnected()) {
                getSendMsgQueue().add(closePacket);
                key.interestOps(SelectionKey.OP_WRITE);
            }

            dispatcher.closeConnection(this);
            key.selector().wakeup();
        }
    }

    /**
     * Kiểm tra kết nối còn hoạt động không
     */
    final boolean isConnected() {
        return key.isValid();
    }

    /**
     * Kiểm tra đã đóng nhưng đang chờ timeout chưa
     */
    final boolean isPendingClose() {
        return pendingCloseUntilMillis != 0 && !closed;
    }

    final boolean isClosed() {
        return closed;
    }

    /**
     * return địa chỉ IP client
     */
    public final String getIP() {
        return ip;
    }

    /**
     * Khoá dùng trong quá trình parse packet
     */
    boolean tryLockConnection() {
        if (locked)
            return false;
        return locked = true;
    }

    void unlockConnection() {
        locked = false;
    }


    /**
     * Được gọi khi <code>dispatcher</code> thực sự xử lý đóng kết nối
     */
    final void disconnect(java.util.concurrent.Executor dcExecutor) {
        synchronized (guard) {
            if (closed)
                return;
            closed = true;
        }

        key.cancel();
        try {
            socketChannel.close();
        } catch (IOException ignored) {
        }

        key.attach(null);
        dcExecutor.execute(this::onDisconnect);
    }

    // ================= ABSTRACT HOOK METHODS =================

    /**
     * Trả về hàng đợi gói tin gửi (có thể là {@link ConcurrentLinkedQueue})
     */
    protected abstract Queue<T> getSendMsgQueue();

    /**
     * @param data
     * @return True nếu dữ liệu được xử lý đúng, False nếu xảy ra lỗi kết nối phải <code>close()</code> ngay bây giờ
     */
    protected abstract boolean processData(ByteBuffer data);

    /**
     * Thís method này sẽ được gọi ở <code>Dispatcher</code> sẽ được lặp lại cho đến khi trả về False
     *
     * @param buffer
     * @return True nếu dữ liệu được ghi vào bộ đệm, False cho biết không còn dữ liệu nào để ghi nữa.
     */
    protected abstract boolean writeData(ByteBuffer buffer);

    /**
     * Được gọi khi đối tượng {@link AConnection} được khởi tạo đầy đủ và đã sẵn sàng để nhận/gửi dữ liệu
     */
    public abstract void initialized();

    /**
     * Như tên hàm
     */
    protected abstract void onDisconnect();

    /**
     * Như tên hàm
     */
    protected abstract void onServerClose();


}
