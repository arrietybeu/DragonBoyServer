package nro.commons.network;

import lombok.Getter;
import nro.commons.network.packet.Acceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.Executor;

/**
 * Bộ điều phối xử lý I/O sử dụng Selector để phân phối các SelectionKey đã sẵn sàng.
 * Mỗi Dispatcher tương ứng với một luồng xử lý selector riêng.
 *
 * @author Arriety
 */
public abstract class Dispatcher extends Thread {

    private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);

    /**
     * {@link Executor} thực thi khi disconnect connection
     */
    private final Executor dcExecutor;

    /**
     * {@link Selector} theo dõi các sự kiện I/O
     */
    @Getter
    protected final Selector selector;

    /**
     * <code>Object</code> khoá (lock) dùng để đồng bộ hoá nhiều thao tác nguy hiểm như gửi dữ liệu, đóng kết nối.
     */
    private final Object gate = new Object();

    public Dispatcher(String name, Executor dcExecutor) throws IOException {
        super(name);
        this.dcExecutor = dcExecutor;
        this.selector = SelectorProvider.provider().openSelector();
    }

    /**
     * Đóng kết nối đang trong danh sách chờ đóng bởi {@link Dispatcher}
     */
    protected abstract void closeConnection(AConnection<?> con);

    /**
     * Phân phối event đòi {@link Selector} sau khi <code>select()</code>
     */
    protected abstract void dispatch() throws IOException;

    /**
     * for loop <code>dispatcher</code> chính: gọi <code>dispatch();</code> liên tục
     */
    @Override
    public void run() {
        while (true) {
            try {
                dispatch();
            } catch (Exception e) {
                log.error("Dispatcher loop error", e);
            }
        }
    }

    /**
     * Đăng ký một {@link SocketChannel} mới và gán {@link SelectionKey} cho connection
     */
    public final void register(SelectableChannel ch, int ops, AConnection<?> att) throws IOException {
        synchronized (gate) {
            selector.wakeup();
            att.setKey(ch.register(selector, ops, att));
        }
    }

    /**
     * Đăng ký một {@link Acceptor} (thường là {@link ServerSocketChannel}) để accept connection mới
     */
    public final SelectionKey register(SelectableChannel ch, int ops, Acceptor att) throws IOException {
        synchronized (gate) {
            selector.wakeup();
            return ch.register(selector, ops, att);
        }
    }

    /**
     * Xử lý khi có kết nối <code>Socket</code> mới được accept
     */
    protected final void accept(SelectionKey key) {
        try {
            ((Acceptor) key.attachment()).accept(key);
        } catch (Exception e) {
            log.error("Accept error", e);
        }
    }

    /**
     * Đọc dữ liệu từ {@link SocketChannel} được gán với {@link SelectionKey}
     */
    protected final void read(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        AConnection<?> con = (AConnection<?>) key.attachment();
        ByteBuffer rb = con.readBuffer;

//        if (Assertion.NetworkAssertion) {
//            assert rb.hasRemaining();
//        }

        int numRead;
        try {
            numRead = channel.read(rb);
        } catch (IOException e) {
            closeConnectionImpl(con);
            return;
        }

        if (numRead == -1) {
            // Remote entity shut the socket down cleanly. Do the same from our end and cancel the channel.
            closeConnectionImpl(con);
            return;
        } else if (numRead == 0) {
            return;
        }


        rb.flip();
        while (rb.remaining() > 2 && rb.remaining() >= (rb.getShort(rb.position()) & 0xFFFF)) {
            // nhận được tin nhắn đầy đủ
            if (!parse(con, rb)) {
                closeConnectionImpl(con);
                return;
            }
        }
        if (rb.hasRemaining()) rb.compact();
        else rb.clear();
    }

    private boolean parse(AConnection<?> con, ByteBuffer buf) {
        int size = (buf.getShort() & 0xFFFF) - 2;
        if (size <= 0) {
            log.warn("Received empty packet without opcode from " + con + ", content: " + NetworkUtils.toHex(buf));
            return false;
        }
        ByteBuffer b = buf.slice().order(buf.order());
        try {
            b.limit(size);
            // read message fully
            buf.position(buf.position() + size);

            return con.processData(b);
        } catch (Exception e) {
            log.error("Error parsing input from " + con + ", packet size: " + size + ", content: " + NetworkUtils.toHex(b), e);
            return false;
        }
    }

    final void write(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        AConnection<?> con = (AConnection<?>) key.attachment();

        int numWrite;
        ByteBuffer wb = con.writeBuffer;
        // We have not written data
        if (wb.hasRemaining()) {
            try {
                numWrite = socketChannel.write(wb);
            } catch (IOException e) {
                closeConnectionImpl(con);
                return;
            }

            if (numWrite == 0) {
                log.info("Write " + numWrite + " ip: " + con.getIP());
                return;
            }

            // Again not all data was send
            if (wb.hasRemaining())
                return;
        }

        while (true) {
            wb.clear();
            boolean writeFailed = !con.writeData(wb);

            if (writeFailed) {
                wb.limit(0);
                break;
            }

            // Attempt to write to the channel
            try {
                numWrite = socketChannel.write(wb);
            } catch (IOException e) {
                closeConnectionImpl(con);
                return;
            }

            if (numWrite == 0) {
                log.info("Write " + numWrite + " ip: " + con.getIP());
                return;
            }

            // not all data was send
            if (wb.hasRemaining())
                return;
        }

        // Test if this build should use assertion. If NetworkAssertion == false javac will remove this code block


        // We wrote away all data, so we're no longer interested in writing on this socket.
        key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
    }

    protected final void closeConnectionImpl(AConnection<?> con) {
        con.disconnect(dcExecutor);
    }


}

