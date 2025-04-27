package nro.login.network.game_server;

import nro.commons.network.packet.BaseServerPacket;

import java.nio.ByteBuffer;

public abstract class GameServerPacket extends BaseServerPacket {

    protected GameServerPacket() {
        super(0);
    }

    public final void write(GameServerConnection con, ByteBuffer buffer) {
        setByteBuff(buffer);
        getByteBuffer().putShort((short) 0);
        writeImpl(con);
        getByteBuffer().flip();
        getByteBuffer().putShort((short) getByteBuffer().limit());
        getByteBuffer().position(0);
    }

    protected abstract void writeImpl(GameServerConnection con);
}
