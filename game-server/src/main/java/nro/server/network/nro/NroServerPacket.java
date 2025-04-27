package nro.server.network.nro;

import nro.commons.network.packet.BaseServerPacket;
import nro.server.network.nro.server_packets.ServerPacketsCommand;

import java.nio.ByteBuffer;

public abstract class NroServerPacket extends BaseServerPacket {

    public static final int MAX_CLIENT_SUPPORTED_PACKET_SIZE = 8192;

    // 8192 - 2 (body length) - 2 (opCode) - 1 (staticServerPacketCode) - 2 (opCode flipped bits)
    public static final int MAX_USABLE_PACKET_BODY_SIZE = MAX_CLIENT_SUPPORTED_PACKET_SIZE - 7;

    protected NroServerPacket() {
        super();
        setCommand(ServerPacketsCommand.getOpcode(getClass()));
    }

    protected NroServerPacket(int command) {
        super(command);
    }

    protected abstract void writeImpl(NroConnection con);

    /**
     * Ghi packet vào buffer, theo đúng format:
     * [1 byte cmd] [2 byte length] [payload]
     * và mã hóa toàn bộ nếu cần.
     */
    public final void write(NroConnection con, ByteBuffer buffer) {
        ByteBuffer temp = ByteBuffer.allocate(2048);
        setByteBuff(temp);

        this.writeImpl(con);

        temp.flip();
        final int bodySize = temp.remaining();
        final NroCrypt crypt = con.getCrypt();

        if (bodySize > NroServerPacket.MAX_USABLE_PACKET_BODY_SIZE) {
            throw new IllegalArgumentException("Packet body too large: " + bodySize);
        }

        System.out.println("write server packet for opcode: " + getCommand() + " | bodySize: " + bodySize);

        if (isSpecialCommand(this.getCommand())) {
            this.writeSpecial(temp, buffer, crypt);
        } else {
            if (!crypt.isSendKey()) {
                buffer.put((byte) getCommand());
                buffer.put((byte) 0);
                buffer.put((byte) bodySize);
                buffer.put(temp);
            } else {
                buffer.put(crypt.encryptByte((byte) getCommand()));
                buffer.put(crypt.encryptByte((byte) (bodySize >> 8)));
                buffer.put(crypt.encryptByte((byte) (bodySize)));
                int posBeforePayload = buffer.position();
                buffer.put(temp);
                int limit = buffer.position();
                for (int i = posBeforePayload; i < limit; i++) {
                    buffer.put(i, crypt.encryptByte(buffer.get(i)));
                }
            }
        }

        buffer.flip();

        con.encrypt();
    }


    protected final void writeSpecial(final ByteBuffer local, ByteBuffer buffer, final NroCrypt crypt) {
        final int bodySize = local.position();
        if (!crypt.isSendKey()) {
            buffer.put((byte) getCommand());

            // Ghi body size theo special format
            int size = bodySize;
            buffer.put((byte) (size % 256 - 128));
            size /= 256;
            buffer.put((byte) (size % 256 - 128));
            size /= 256;
            buffer.put((byte) (size % 256 - 128));

            buffer.put(local);
        } else {
            buffer.put(crypt.encryptByte((byte) getCommand()));
            // Ghi body size theo special format (mã hóa từng byte)
            int size = bodySize;
            buffer.put(crypt.encryptByte((byte) (size % 256 - 128)));
            size /= 256;
            buffer.put(crypt.encryptByte((byte) (size % 256 - 128)));
            size /= 256;
            buffer.put(crypt.encryptByte((byte) (size % 256 - 128)));

            int posBeforePayload = buffer.position();
            buffer.put(local);

            // Mã hóa phần payload
            int limit = buffer.position();
            for (int i = posBeforePayload; i < limit; i++) {
                buffer.put(i, crypt.encryptByte(buffer.get(i)));
            }
        }
    }

    protected static boolean isSpecialCommand(int cmd) {
        return cmd == -32 || cmd == -66 || cmd == 11 || cmd == -67 || cmd == -74 || cmd == -87 || cmd == 66 || cmd == 12;
    }


}
