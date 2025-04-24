package nro.commons.network.packet;

import lombok.Setter;

import java.nio.ByteBuffer;

@Setter
public abstract class BaseServerPacket extends BasePacket {

    public ByteBuffer byteBuffer;

    protected BaseServerPacket() {
        super();
    }

    protected BaseServerPacket(int command) {
        super(command);
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
    protected final void writeShort(int value) {
        byteBuffer.putShort((short) value);
    }

    /**
     * Write byte to buffer.
     *
     * @param value
     */
    protected final void writeByte(int value) {
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
    protected final void writeUTF(String str) {
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
     * Write byte array to buffer.
     *
     * @param data
     */
    protected final void writeByte(byte[] data) {
        byteBuffer.put(data);
    }

}
