package nro.commons.network.packet;

import lombok.Getter;
import lombok.Setter;
import nro.commons.network.AConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public abstract class BaseClientPacket<T extends AConnection<?>> extends BasePacket implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(BaseClientPacket.class);
    private static final Set<Integer> partiallyReadPackets = ConcurrentHashMap.newKeySet();

    private T client;

    private ByteBuffer buf;

    public BaseClientPacket(int opcode) {
        this(null, opcode);
    }

    public BaseClientPacket(ByteBuffer buf, int opcode) {
        super(opcode);
        this.buf = buf;
    }

    protected abstract void readImpl();

    public final int getRemainingBytes() {
        return buf.remaining();
    }

}
