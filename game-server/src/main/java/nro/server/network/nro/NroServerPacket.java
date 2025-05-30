package nro.server.network.nro;

import nro.commons.network.Crypt;
import nro.commons.network.packet.BaseServerPacket;
import nro.server.configs.network.NetworkConfig;
import nro.server.network.nro.server_packets.ServerPacketsCommand;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

public abstract class NroServerPacket extends BaseServerPacket {

    // 8192 - 2 (body length) - 2 (opCode) - 1 (staticServerPacketCode) - 2 (opCode flipped bits)
    public static final int MAX_USABLE_PACKET_BODY_SIZE = NetworkConfig.WRITE_BUFFER_SIZE;

    /**
     * Checks if the given command is a special command.
     *
     * <p>Special commands are commands that are handled by the server differently than regular commands.
     * These commands are used for things like encryption, compression, and internal server operations.</p>
     *
     * @param cmd - The command to check.
     * @return true if the command is special, false otherwise.
     */
    protected static boolean isSpecialCommand(int cmd) {
        return cmd == -32 || cmd == -66 || cmd == 11 || cmd == -67 || cmd == -74 || cmd == -87 || cmd == 66 || cmd == 12;
    }

    protected NroServerPacket() {
        super();
        setCommand(ServerPacketsCommand.getOpcode(getClass()));
    }

    protected NroServerPacket(int command) {
        super(command);
    }

    protected abstract void writeImpl(NroConnection con) throws RuntimeException;

    /**
     * Ghi một packet vào ByteBuffer để chuẩn bị gửi cho client.
     *
     * @param con    - NroConnection tương ứng với client mà mình đang gửi packet tới.
     *               - Task: chứa session, crypt key, trạng thái send/receive, buffer đọc/ghi...
     * @param buffer - ByteBuffer chính để chứa dữ liệu đã build xong, chuẩn bị write ra socket.
     *               - Task: buffer này sẽ được SocketChannel.write(buffer) ngay sau khi ghi xong.
     *               - buffer này thường chính là con.writeBuffer (ByteBuffer đã được cấp phát sẵn khi accept kết nối).
     */
    public final void write(NroConnection con, ByteBuffer buffer) throws RuntimeException {

        ByteBuffer temp = TEMP_BUFFER.get();
        temp.clear();
        setByteBuff(temp);
        this.writeImpl(con);

        temp.flip();
        final int bodySize = temp.remaining();
        final Crypt crypt = con.getCrypt();

        if (!isSpecialCommand(this.getCommand()) &&
                bodySize > NroServerPacket.MAX_USABLE_PACKET_BODY_SIZE) {
            throw new IllegalArgumentException("Packet body too large: " + bodySize);
        }

//        System.out.println("write server packet for opcode: " + getCommand() + " | bodySize: " + bodySize);

        if (buffer.remaining() < bodySize + 3) {
            throw new BufferOverflowException();
        }

        if (isSpecialCommand(this.getCommand())) {
            this.writeSpecial(temp, buffer, crypt, bodySize);
        } else {
            if (!crypt.isSendKey()) {
                buffer.put((byte) getCommand());
                buffer.put((byte) 0);
                buffer.put((byte) bodySize);
                buffer.put(temp);// put bytes for writeImpl
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

    protected final void writeSpecial(final ByteBuffer local, ByteBuffer buffer, final Crypt crypt, int bodySize) throws RuntimeException {
//        final int bodySize = local.position();
        if (!crypt.isSendKey()) {
            buffer.put((byte) getCommand());
            int size = bodySize;
            buffer.put((byte) (size % 256 - 128));
            size /= 256;
            buffer.put((byte) (size % 256 - 128));
            size /= 256;
            buffer.put((byte) (size % 256 - 128));

            buffer.put(local);
        } else {
            buffer.put(crypt.encryptByte((byte) getCommand()));

            int size = bodySize;
            buffer.put(crypt.encryptByte((byte) (size % 256 - 128)));
            size /= 256;
            buffer.put(crypt.encryptByte((byte) (size % 256 - 128)));
            size /= 256;
            buffer.put(crypt.encryptByte((byte) (size % 256 - 128)));

            int posBeforePayload = buffer.position();
            buffer.put(local);

            int limit = buffer.position();
            for (int i = posBeforePayload; i < limit; i++) {
                buffer.put(i, crypt.encryptByte(buffer.get(i)));
            }
        }
    }

}
