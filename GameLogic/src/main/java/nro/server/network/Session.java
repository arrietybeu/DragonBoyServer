package nro.server.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.Setter;
import nro.controller.Controller;
import nro.model.player.Player;
import nro.model.template.entity.ClientInfo;
import nro.model.template.entity.SessionInfo;
import nro.model.template.entity.UserInfo;
import nro.server.manager.SessionManager;
import nro.server.manager.UserManager;
import nro.server.LogServer;

/**
 * @author {@code Arriety}
 */
// @SuppressWarnings("ALL")
@Getter
@Setter
public final class Session {

    private static AtomicInteger baseId = new AtomicInteger(0);

    private static final int MAX_ID = Integer.MAX_VALUE - 1;

    private final Controller controller;
    private final ClientInfo clientInfo;
    private final SessionInfo sessionInfo;

    public ConcurrentLinkedQueue<Message> listMessage;// new Message(tat)
    public ExecutorService executorService;
    private Socket socket;
    private MessageSender messageSender;
    private MessageReceiver messageReceiver;

    private UserInfo userInfo;
    private Player player;

    public Session(Socket socket, Controller controller) {
        this.clientInfo = new ClientInfo();
        this.sessionInfo = new SessionInfo();
        this.controller = controller;
        try {
            this.sessionInfo.setId(getBaseId());
            this.sessionInfo.setConnected(true);
            this.socket = socket;
            this.socket.setKeepAlive(true);
            this.sessionInfo.setIp(this.socket.getInetAddress().getHostAddress());
            this.executorService = Executors.newFixedThreadPool(2);
            this.initCommunication();
            SessionManager.getInstance().add(this);
        } catch (Exception e) {
            this.handleInitializationError();
            LogServer.LogException("Error Session: " + e.getMessage());
        }
        // LogServer.DebugLogic("Session connect: " + sessionInfo.getId() + "-" +
        // Thread.activeCount() + " session size: " +
        // SessionManager.getInstance().getSizeSession());
    }

    private void initCommunication() throws IOException {
        this.messageSender = new MessageSender(this, new DataOutputStream(this.socket.getOutputStream()));
        this.messageReceiver = new MessageReceiver(this, new DataInputStream(this.socket.getInputStream()));
        this.messageReceiver.startReadMessage();
    }

    public void sendSessionKey() {
        this.messageSender.startSend();
    }

    /**
     * Sử dụng phương thức {@code poll} để lấy phần tử đầu tiên của danh sách
     * (list) và đồng thời xóa phần tử đó khỏi danh sách.
     *
     * <p>
     * Phương thức {@code poll} trả về:
     * <ul>
     * <li>Phần tử đầu tiên của danh sách, nếu danh sách không rỗng.</li>
     * <li>{@code null}, nếu danh sách rỗng.</li>
     * </ul>
     * </p>
     */

    public Message getListMessage() {
        return this.listMessage.isEmpty() ? null : this.listMessage.poll();
    }

    public boolean isExecutorServiceRunning() {
        return !this.executorService.isShutdown() && !this.executorService.isTerminated();
    }

    /**
     * * Server có 2 kiểu gửi dữ liệu cho client:
     *
     * <ul>
     * <li>1. Gửi trực tiếp thông qua socket (phương thức
     * {@link #doSendMessage}).</li>
     * <li>2. Gửi thông qua list_msg (phương thức {@link #sendMessage}).</li>
     * </ul>
     *
     * <p>
     * * Phương thức {@code doSendMessage} sẽ gửi trực tiếp ngay lập tức cho client,
     * phù hợp với
     * các tin nhắn quan trọng như thông báo gửi dữ liệu ảnh hoặc byte data.
     * </p>
     *
     * <p>
     * * Trong khi đó, phương thức {@code sendMessage} sẽ nạp dữ liệu message vào
     * list
     * và được một thread send message tự động duyệt qua lít để gửi.
     * Cách này phù hợp với các tin nhắn sử dụng nhiều lần, giúp tránh việc gửi liên
     * tục quá nhanh.
     * </p>
     *
     * <p>
     * * Phương thức {@code this.getClientInfo().updateLastActiveTime()}
     * sau khi connect thành công thì server sẽ quét tất cả các message trong hash
     * map {@code Session}
     * chỉ khi server và client giao tiếp với nhau và cập nhập lại
     * {@code lastActiveTime} thì sẽ không bị
     * disconnect khỏi server nếu client và server vẫn duy trì giao típ thì sẽ không
     * bị disconnect
     * 
     * <pre>
     * {@code
     * this.getClientInfo().updateLastActiveTime();
     * }
     * </pre>
     * </p>
     *
     * <p>
     * 
     * <pre>
     *  * VÍ DỤ:
     *  <pre>
     * {@code
     * public void sendMessage() {
     *     Message m = new Message(1);
     *     this.list_msg.offer(m);// list msg được nạp thêm 1 dữ liệu msg
     *     this.getClientInfo().updateLastActiveTime();// cập nhật thời gian gửi tin nhắn
     * }
     * }
     *  </pre>
     * </pre>
     * </p>
     */

    public void sendMessage(Message m) {
        if (!this.isSocketValid()) {
            return;
        }
        try {
            if (this.sessionInfo.getConnect()) {
                this.listMessage.offer(m);
                this.getClientInfo().updateLastActiveTime();
            }
        } catch (Exception e) {
            LogServer.LogException("Error sendMessage: " + e.getMessage());
            LogServer.DebugLogic(
                    "Socket State: isClosed=" + this.socket.isClosed() + ", isConnected=" + this.socket.isConnected());
        }
    }

    public boolean isClosed() {
        return socket == null || socket.isClosed();
    }

    public void doSendMessage(Message message) {
        try {
            this.messageSender.doSendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error doSendMessage: " + e.getMessage());
        }
    }

    private boolean isSocketValid() {
        return this.socket != null && !this.socket.isClosed() && this.socket.isConnected();
    }

    /**
     * Phương thức {@code disconnect} thực hiện việc đóng kết nối giữa server và
     * client.
     *
     * <p>
     * Khi được gọi, phương thức này sẽ:
     * <ul>
     * <li>Đặt trạng thái kết nối của {@link SessionInfo} là không còn kết nối.</li>
     * <li>Đặt các thông số send key vị trí send key hiện tại liên quan đến đọc và
     * ghi dữ liệu về 0.</li>
     * <li>Đóng các tài nguyên quan trọng như:
     * <ul>
     * <li>{@link ExecutorService}: Ngừng thực hiện các tác vụ đa luồng.</li>
     * <li>{@link Socket}: Đóng socket hiện tại nếu còn mở.</li>
     * <li>{@link MessageReceiver} và {@link MessageSender}: Giải phóng các luồng
     * đọc/ghi message.</li>
     * <li>{@code list_msg}: Xóa tất cả tin nhắn trong hàng đợi và giải phóng bộ
     * nhớ.</li>
     * <li>{@link Controller} và {@link ClientInfo}: Đặt về {@code null} để giải
     * phóng tài nguyên.</li>
     * <li>{@link UserInfo}: Loại bỏ người dùng khỏi {@link UserManager} và giải
     * phóng tài nguyên.</li>
     * </ul>
     * </li>
     * <li>Gọi {@code System.gc()} để kích hoạt việc thu gom rác (Garbage
     * Collection) nhằm giải phóng bộ nhớ không còn sử dụng (Giờ không gọi nữa
     * rồi).</li>
     * </ul>
     * </p>
     *
     * <p>
     * Nếu xảy ra bất kỳ lỗi nào trong quá trình thực hiện, ngoại lệ sẽ được ghi lại
     * thông qua {@link LogServer}.
     * </p>
     *
     * @throws Exception nếu có lỗi trong quá trình ngắt kết nối hoặc giải phóng tài
     *                   nguyên.
     */

    private void disconnect() {
        sessionInfo.setConnected(false);
        sessionInfo.setLogin(false);
        sessionInfo.curR = 0;
        sessionInfo.curW = 0;
        executorService.shutdown();

        try {
            if (socket != null) {
                socket.close();
            }

            if (messageReceiver != null) {
                messageReceiver.close();
                messageReceiver = null;
            }

            if (messageSender != null) {
                messageSender.close();
                messageReceiver = null;
            }

            if (listMessage != null) {
                listMessage.clear();
            }

            if (this.userInfo != null) {
                UserManager.getInstance().remove(userInfo);
            }

        } catch (Exception e) {
            LogServer.LogException("Error during disconnect: " + e.getMessage());
        }
    }

    public void close() {
        this.disconnect();
    }

    public static int getBaseId() {
        int newId = baseId.updateAndGet(id -> (id >= MAX_ID) ? 0 : id + 1);
        return newId;
    }

    private void handleInitializationError() {
        try {
            if (this.socket != null && !this.socket.isClosed()) {
                this.socket.close();
            }
            SessionManager.getInstance().kickSession(this);
        } catch (IOException e) {
            LogServer.LogException("error during cleanup: " + e.getMessage());
        }
    }

}
