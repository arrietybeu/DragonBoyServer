package nro.commons.network;

import nro.commons.utils.NetworkUtils;
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
     * Đăng ký một {@link Acceptor} (thường là {@link ServerSocketChannel}) để accept connection mới
     */
//    public final SelectionKey register(SelectableChannel ch, int ops, Acceptor att) throws IOException {
//        synchronized (gate) {
//            selector.wakeup();
//            return ch.register(selector, ops, att);
//        }
//    }

    public final SelectionKey register(SelectableChannel ch, int ops, Acceptor att) throws IOException {
        synchronized (gate) {
            SelectionKey key = ch.register(selector, ops, att);
            selector.wakeup();
            return key;
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
        SocketChannel socketChannel = (SocketChannel) key.channel();
        AConnection<?> con = (AConnection<?>) key.attachment();

        ByteBuffer rb = con.readBuffer;

        int numRead;
        try {
            numRead = socketChannel.read(rb);
        } catch (IOException e) {
            closeConnectionImpl(con);
            return;
        }

        if (numRead == -1) {
            closeConnectionImpl(con);
            return;
        } else if (numRead == 0) {
            return;
        }

        rb.flip();

        while (true) {
            int pos = rb.position();

            if (rb.remaining() < 3) {
                rb.position(pos);
                break;
            }

            byte cmd = rb.get(pos);
            byte b1 = rb.get(pos + 1);
            byte b2 = rb.get(pos + 2);

            boolean isEncrypted = con.getCrypt().isSendKey();
            if (isEncrypted) {
                cmd = con.getCrypt().decryptByte(cmd);
                b1 = con.getCrypt().decryptByte(b1);
                b2 = con.getCrypt().decryptByte(b2);
            }

            int bodyLength = ((b1 & 0xFF) << 8) | (b2 & 0xFF);
            int fullPacketLength = 1 + 2 + bodyLength;

            if (rb.remaining() < fullPacketLength) {
                rb.position(pos);
                break;
            }

            ByteBuffer packetBuf = rb.slice();
            packetBuf.limit(fullPacketLength);

            rb.position(pos + fullPacketLength);

            try {
                if (!con.processData(packetBuf)) {
                    closeConnectionImpl(con);
                    return;
                }
            } catch (Exception e) {
                log.error("Error processing packet from {}, content: {}", con, NetworkUtils.toHex(packetBuf), e);
                closeConnectionImpl(con);
                return;
            }
        }

        if (rb.hasRemaining()) {
            rb.compact();
        } else {
            rb.clear();
        }
    }

    /**
     * Parse dữ liệu từ buffer, tách ra đúng từng message theo định dạng packet cũ.
     * Format packet: [1 byte CMD] + [2 byte LENGTH] + PAYLOAD[length]
     */
    private boolean parse(AConnection<?> con, ByteBuffer buf) {
        int size = (buf.getShort() & 0xFFFF) - 2;
        if (size <= 0) {
            log.warn("Received empty packet without opcode from {}, content: {}", con, NetworkUtils.toHex(buf));
            return false;
        }

        ByteBuffer b = buf.slice().order(buf.order());
        try {
            b.limit(size);
            buf.position(buf.position() + size);
            return con.processData(b);
        } catch (Exception e) {
            log.error("Error parsing input from {}, packet size: {}, content: {}", con, size, NetworkUtils.toHex(b), e);
            return false;
        }
    }

    final void write(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        AConnection<?> con = (AConnection<?>) key.attachment();

        int numWrite;
        ByteBuffer wb = con.writeBuffer;

        if (wb.hasRemaining()) {
            try {
                numWrite = socketChannel.write(wb);
            } catch (IOException e) {
                closeConnectionImpl(con);
                return;
            }

            if (numWrite == 0) {
                log.info("Write {} ip: {}", numWrite, con.getIP());
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
                log.info("nit me may Write {} ip: {}", numWrite, con.getIP());
                return;
            }

            // not all data was send
            if (wb.hasRemaining())
                return;
        }

        key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
    }

    protected final void closeConnectionImpl(AConnection<?> con) {
        con.disconnect(dcExecutor);
    }

    public final Selector selector() {
        return this.selector;
    }

}

