package nro.server.network.nro;

import nro.commons.network.packet.BaseServerPacket;
import nro.server.network.NroConnection;
import nro.server.network.nro.server_packets.ServerPacketsCommand;

import java.nio.ByteBuffer;

public class NroServerPacket extends BaseServerPacket {

    public static final int MAX_CLIENT_SUPPORTED_PACKET_SIZE = 8192;
    public static final int MAX_USABLE_PACKET_BODY_SIZE = MAX_CLIENT_SUPPORTED_PACKET_SIZE - 7; // 8192 - 2 (body length) - 2 (opCode) - 1 (staticServerPacketCode) - 2 (opCode flipped bits)

    public ByteBuffer buf;

    protected NroServerPacket() {
        super();
        setCommand(ServerPacketsCommand.getOpcode(getClass()));
    }

    protected NroServerPacket(int command) {
        super(command);
    }

    protected void writeImpl(NroConnection con) {
    }

    /**
     * Ghi packet vào buffer, theo đúng format:
     * [1 byte cmd] [2 byte length] [payload]
     * và mã hóa toàn bộ nếu cần.
     */
    public final void write(NroConnection con, ByteBuffer buffer) {
//        // --- Ghi payload tạm vào buffer phụ ---
//        ByteBuffer tempBuf = ByteBuffer.allocate(MAX_USABLE_PACKET_BODY_SIZE);
//        this.setByteBuffer(tempBuf);
//        writeImpl(con); // Ghi dữ liệu cụ thể
//        tempBuf.flip();
//
//        byte[] payload = new byte[tempBuf.remaining()];
//        tempBuf.get(payload);
//        int size = payload.length;
//
//        this.setByteBuffer(buffer); // Ghi vào buffer chính
//        boolean encrypted = con.getClientInfo().isSendKeyComplete();
//        byte cmd = (byte) getCommand();
//
//        // --- write msg ---
//        buffer.put(encrypted ? con.writeKey(cmd) : cmd);
//
//        // --- write Length ---
//        if (encrypted) {
//            buffer.put(con.writeKey((byte) (size >> 8)));
//            buffer.put(con.writeKey((byte) (size & 0xFF)));
//        } else {
//            buffer.putShort((short) size);
//        }
//
//        // --- write Payload ---
//        if (size > 0) {
//            if (encrypted) {
//                for (byte b : payload) {
//                    buffer.put(con.writeKey(b));
//                }
//            } else {
//                buffer.put(payload);
//            }
//        }
//
//        con.getSessionInfo().sendByteCount += (1 + 2 + size);
    }


}
