package nro.server.network.nro;

import nro.commons.network.packet.BaseServerPacket;

import java.nio.ByteBuffer;

/**
 * Gói tin server -> client trong game NRO
 *
 * @author Arriety
 */
public abstract class NroServerPacket extends BaseServerPacket {

    public static final int MAX_CLIENT_SUPPORTED_PACKET_SIZE = 1024;

    protected NroServerPacket() {
        super();
        setCommand(ServerPacketsCommand.getOpcode(getClass()));
    }

    protected NroServerPacket(int command) {
        super(command);
    }

    public final void write(NroConnection con, ByteBuffer buffer) {
        setByteBuffer(buffer);

        byte command = (byte) getCommand();
        if (con.isEncrypted()) {
            command = con.writeKey(command);
        }
        buffer.put(command); // ghi command

        writeImpl(con); // ghi nội dung vào byteBuffer nội bộ

        int size = byteBuffer.position();
        byteBuffer.flip();

        if (con.isBigPacket(command)) {
            byte b1 = (byte) (size);
            byte b2 = (byte) (size >> 8);
            byte b3 = (byte) (size >> 16);
            if (con.isEncrypted()) {
                buffer.put((byte) (con.writeKey(b1) - 128));
                buffer.put((byte) (con.writeKey(b2) - 128));
                buffer.put((byte) (con.writeKey(b3) - 128));
            } else {
                buffer.put(b1);
                buffer.put(b2);
                buffer.put(b3);
            }
        } else {
            byte b1 = (byte) (size >> 8);
            byte b2 = (byte) (size);
            if (con.isEncrypted()) {
                buffer.put(con.writeKey(b1));
                buffer.put(con.writeKey(b2));
            } else {
                buffer.putShort((short) size);
            }
        }

        // ghi nội dung
        for (int i = 0; i < size; i++) {
            byte b = byteBuffer.get();
            if (con.isEncrypted()) {
                b = con.writeKey(b);
            }
            buffer.put(b);
        }
    }

    protected abstract void writeImpl(NroConnection con);

    // Viết hàm hỗ trợ ghi byte dữ liệu
    protected final void writeC(int value) {
        byteBuffer.put((byte) value);
    }

    protected final void writeS(String value) {
        if (value != null) {
            for (char c : value.toCharArray()) {
                byteBuffer.putChar(c);
            }
        }
        byteBuffer.putChar('\0');
    }

    protected final void writeB(byte[] data) {
        byteBuffer.put(data);
    }

    protected final void writeH(int value) {
        byteBuffer.putShort((short) value);
    }

    protected final void writeD(int value) {
        byteBuffer.putInt(value);
    }

    @Override
    protected int getOpCodeZeroPadding() {
        return 2; // NRO chắc chỉ dùng opcode 1 byte hoặc 2 byte
    }
}
