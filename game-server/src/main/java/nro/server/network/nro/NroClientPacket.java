package nro.server.network.nro;

import nro.commons.network.packet.BaseClientPacket;
import nro.server.system.LogServer;

import java.util.Set;

public abstract class NroClientPacket extends BaseClientPacket<NroConnection> {

    private final Set<NroConnection.State> validStates;

    protected NroClientPacket(int opcode, Set<NroConnection.State> validStates) {
        super(opcode);
        this.validStates = validStates;
    }

    protected void sendPacket(NroServerPacket msg) {
        getConnection().sendPacket(msg);
    }


    public final boolean isValid() {
        return validStates.contains(getConnection().getState());
    }

    @Override
    public final void run() {
        try {
            if (isValid()) // run only if packet is still valid (connection state didn't change, for example due to logout)
                runImpl();
        } catch (Exception e) {
            LogServer.LogException("Error handling client packet from " + getConnection() + ": " + this, e);
        }
    }
}
