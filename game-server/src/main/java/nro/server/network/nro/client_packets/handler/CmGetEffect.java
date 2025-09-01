package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;

import java.util.Set;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.GET_EFFDATA, validStates = {NroConnection.State.IN_GAME})
public class CmGetEffect extends NroClientPacket {

    private int idEffect;

    public CmGetEffect(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        this.idEffect = this.readShort();
    }

    @Override
    protected void runImpl() {
    }
}
