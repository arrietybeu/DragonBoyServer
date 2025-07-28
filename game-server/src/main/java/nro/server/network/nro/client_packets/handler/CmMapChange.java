package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;

import java.util.Set;

/**
 * @author Arriety
 */

@AClientPacketHandler(command = ConstsCmd.MAP_CHANGE, validStates = {NroConnection.State.IN_GAME})
public class CmMapChange extends CmMapChangeBase {

    public CmMapChange(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }
}
