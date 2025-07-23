package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;

import java.util.Set;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.GET_BAG, validStates = {NroConnection.State.IN_GAME})
public class CmGetBag extends NroClientPacket {

    public CmGetBag(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        var idFlagBag = this.readShort();
        System.out.println("client get id flag bag: " + idFlagBag);
    }

    @Override
    protected void runImpl() {

    }
}
