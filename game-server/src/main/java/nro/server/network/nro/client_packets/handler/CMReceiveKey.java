package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.commons.network.Crypt;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;

import java.util.Set;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.GET_SESSION_ID, validStates = { NroConnection.State.CONNECTED })
public class CMReceiveKey extends NroClientPacket {

    public CMReceiveKey(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        Crypt crypt = getConnection().getCrypt();
        if (crypt == null) {
            throw new IllegalStateException("Crypt is not initialized - " + getConnection());
        }
        crypt.resetKeyIndex();
        crypt.setSendKey(true);
    }
}
