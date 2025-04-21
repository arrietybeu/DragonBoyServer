package nro.commons.network.packet;

import java.nio.ByteBuffer;

public abstract class BaseServerPacket extends BasePacket {

    public ByteBuffer byteBuffer;

    protected BaseServerPacket() {
        super();
    }

    protected BaseServerPacket(int command) {
        super(command);
    }

    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

}
