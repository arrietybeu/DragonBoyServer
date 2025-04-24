package nro.commons.network.packet;

import lombok.Getter;
import lombok.Setter;
import nro.commons.network.AConnection;
import nro.commons.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public abstract class BaseClientPacket<T extends AConnection<?>> extends BasePacket implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(BaseClientPacket.class);

    private static final Set<Integer> partiallyReadPackets = ConcurrentHashMap.newKeySet();

    private T client;

    private ByteBuffer buf;

    public BaseClientPacket(int command) {
        this(null, command);
    }

    public BaseClientPacket(ByteBuffer buf, int command) {
        super(command);
        this.buf = buf;
    }

    public void setConnection(T client) {
        this.client = client;
    }

    public void setBuffer(ByteBuffer buf) {
        this.buf = buf;
    }

    public final boolean read() {
        int startPos = buf.position();
        try {
            readImpl();
            if (getRemainingBytes() > 0 && partiallyReadPackets.add(getCommand())) {
                log.warn("{} was not fully read! Last {} bytes were not read from buffer:\n{}", this, getRemainingBytes(), NetworkUtils.toHex(buf, startPos, buf.limit()));
            }
            return true;
        } catch (Exception ex) {
            String msg = "Reading failed for packet " + this + ". Buffer Info";
            if (getRemainingBytes() > 0)
                msg += " (last " + getRemainingBytes() + " bytes were not read)";
            msg += ":\n" + NetworkUtils.toHex(buf, startPos, buf.limit());
            log.error(msg, ex);
            return false;
        }
    }

    public final int getRemainingBytes() {
        return buf.remaining();
    }

    protected abstract void readImpl();

    protected abstract void runImpl();

    protected final int readInt() {
        try {
            return buf.getInt();
        } catch (BufferUnderflowException e) {
            log.error("Missing readInt for: {} (sent from {})", this, client, e);
            return 0;
        }
    }

    protected final byte readByte() {
        try {
            return buf.get();
        } catch (BufferUnderflowException e) {
            log.error("Missing readByte for: {} (sent from {})", this, client, e);
            return 0;
        }
    }

    /**
     * Read unsigned byte from this packet buffer.
     *
     * @return int
     */
    protected final int readUnsignedByte() {
        try {
            return buf.get() & 0xFF;
        } catch (BufferUnderflowException e) {
            log.error("Missing readUnsignedByte for: " + this + " (sent from " + client + ")", e);
            return 0;
        }
    }

    /**
     * Read short from this packet buffer.
     *
     * @return short
     */
    protected final short readShort() {
        try {
            return buf.getShort();
        } catch (BufferUnderflowException e) {
            log.error("Missing readShort for: {} (sent from {})", this, client, e);
            return 0;
        }
    }

    /**
     * Read unsigned short from this packet buffer.
     *
     * @return int
     */
    protected final int readUnsignedShort() {
        try {
            return buf.getShort() & 0xFFFF;
        } catch (BufferUnderflowException e) {
            log.error("Missing readUnsignedShort for: {} (sent from {})", this, client, e);
        }
        return 0;
    }

    /**
     * Read double from this packet buffer.
     *
     * @return double
     */
    protected final double readDouble() {
        try {
            return buf.getDouble();
        } catch (BufferUnderflowException e) {
            log.error("Missing readDouble for: {} (sent from {})", this, client, );
        }
        return 0;
    }

    /**
     * Read Float from this packet buffer.
     *
     * @return Float
     */
    protected final float readFloat() {
        try {
            return buf.getFloat();
        } catch (BufferUnderflowException e) {
            log.error("Missing readFloat for: {} (sent from {})", this, client, e);
            return 0;
        }
    }

    /**
     * Read long from this packet buffer.
     *
     * @return long
     */
    protected final long readLong() {
        try {
            return buf.getLong();
        } catch (BufferUnderflowException e) {
            log.error("Missing readLong for: {} (sent from {})", this, client, e);
        }
        return 0;
    }

    /**
     * Read String from this packet buffer.
     *
     * @return String
     */
//    protected final String readUTF() {
//        try {
//            int len = buf.getShort() & 0xFFFF;
//            byte[] utfBytes = new byte[len];
//            buf.get(utfBytes);
//            return new String(utfBytes, StandardCharsets.UTF_8);
//        } catch (Exception e) {
//            log.error("Missing S for: {} (sent from {})", this, client, e);
//            return "";
//        }
//    }

    protected final String readUTF() {
        try {
            int len = this.readShort(); // 2 byte độ dài
            byte[] bytes = new byte[len];
            buf.get(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Missing UTF string for: {}", this, e);
            return "";
        }
    }


    /**
     * Read n bytes from this packet buffer, n = length.
     *
     * @param length
     * @return byte[]
     */
    protected final byte[] readBytes(int length) {
        byte[] result = new byte[length];
        try {
            buf.get(result);
        } catch (BufferUnderflowException e) {
            log.error("Missing byte[] for: {} (sent from {})", this, client, e);
        }
        return result;
    }

    public final T getConnection() {
        return client;
    }

}
