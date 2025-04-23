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

    public BaseClientPacket(int command) {
        this(null, command);
    }

    public BaseClientPacket(ByteBuffer buf, int command) {
        super(command);
        this.buf = buf;
    }

    public final int getRemainingBytes() {
        return buf.remaining();
    }

    public final T getConnection() {
        return client;
    }

    protected abstract void runImpl();

    protected abstract void readImpl();

}
