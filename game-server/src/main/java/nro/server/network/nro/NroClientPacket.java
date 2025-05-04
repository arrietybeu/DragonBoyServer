package nro.server.network.nro;

import nro.commons.network.packet.BaseClientPacket;

import nro.server.network.nro.NroConnection.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Set;

public abstract class NroClientPacket extends BaseClientPacket<NroConnection> {

    protected static final Logger log = LoggerFactory.getLogger(NroClientPacket.class);

    private final Set<State> validStates;

    public NroClientPacket(int command, Set<State> validStates) {
        super(command);
        this.validStates = validStates;
    }

    protected void sendPacket(NroServerPacket message) {
        this.getConnection().sendPacket(message);
    }

    public final boolean isValid() {
        return validStates.contains(getConnection().getState());
    }

    @Override
    public void run() {
        try {
            if (isValid())
                runImpl();
        } catch (Throwable e) {
            log.error("Error handling client packet from {}: {}", getConnection(), this, e);
        }
    }
}
