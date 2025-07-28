package nro.commons.network.packet;

import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

@Getter
@Setter
public abstract class BaseServerPacket extends BasePacket {

    private static final int TEMP_BUFFER_SIZE = 4_175_000;

    protected static final ThreadLocal<ByteBuffer> TEMP_BUFFER =
            ThreadLocal.withInitial(() -> ByteBuffer.allocate(TEMP_BUFFER_SIZE));

    public ByteBuffer byteBuffer;

    protected BaseServerPacket() {
        super();
    }

    protected BaseServerPacket(int command) {
        super(command);
    }

    /**
     * @param buf the buf to set
     */
    public void setByteBuff(ByteBuffer buf) {
        this.byteBuffer = buf;
    }

    /**
     * Write int to buffer.
     *
     * @param value
     */
    protected final void writeInt(int value) {
        byteBuffer.putInt(value);
    }

    /**
     * Write short to buffer.
     *
     * @param value
     */
    public final void writeShort(int value) {
        byteBuffer.putShort((short) value);
    }

    /**
     * Write byte to buffer.
     *
     * @param value
     */
    public final void writeByte(int value) {
        byteBuffer.put((byte) value);
    }

    /**
     * Write byte to buffer. Not cast
     *
     * @param value
     */
    protected final void writeByte(byte value) {
        byteBuffer.put(value);
    }

    protected final void writeUnsignedByte(int value) {
        if (value < 0 || value > 255)
            throw new IllegalArgumentException("Gia tri unsigned byte phai tu 0 den 255");
        writeByte(value);
    }

    /**
     * Write double to buffer.
     *
     * @param value
     */
    protected final void writeDouble(double value) {
        byteBuffer.putDouble(value);
    }

    /**
     * Write float to buffer.
     *
     * @param value
     */
    protected final void writeFloat(float value) {
        byteBuffer.putFloat(value);
    }

    /**
     * Write long to buffer.
     *
     * @param value
     */
    protected final void writeLong(long value) {
        byteBuffer.putLong(value);
    }

    /**
     * Write String to buffer
     *
     * @param str
     */
    public final void writeUTF(String str) {
        if (str == null || str.isEmpty()) {
            writeShort(0);
            return;
        }

        byte[] utfBytes = str.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        if (utfBytes.length > Short.MAX_VALUE) {
            throw new IllegalArgumentException("UTF string too long for writeUTF: " + utfBytes.length);
        }

        writeShort(utfBytes.length);
        byteBuffer.put(utfBytes);
    }

    /**
     * @param value
     */
    public final void writeBoolean(boolean value) {
        this.writeByte((value ? 1 : 0));
    }

    /**
     * Write byte array to buffer.
     *
     * @param data
     */
    protected final void writeBytes(byte[] data, String... path) {
//        System.out.println("Write byte array length = " + data.length + ", remaining = " + byteBuffer.remaining() +
//                ", path = " + String.join(",", path));
        ensureCapacity(data.length);
        byteBuffer.put(data);
    }

    protected void ensureCapacity(int sizeToWrite) {
        if (byteBuffer == null) {
            throw new IllegalStateException("byteBuffer chưa được set! Gọi setByteBuff(ByteBuffer) trước khi ghi.");
        }

        if (byteBuffer.remaining() < sizeToWrite) {
            int newSize = byteBuffer.capacity() + sizeToWrite + 1024;
            ByteBuffer newBuffer = ByteBuffer.allocate(newSize);
            byteBuffer.flip();
            newBuffer.put(byteBuffer);
            byteBuffer = newBuffer;
            TEMP_BUFFER.set(newBuffer);

        }
    }


}
