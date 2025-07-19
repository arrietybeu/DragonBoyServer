package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.client_packets.AClientPacketHandler;

/**
 * @author Arriety
 */

@AClientPacketHandler(command = ConstsCmd.UPDATE_CAPTION, validStates = {NroConnection.State.IN_GAME})
public class CmUpdateCaption extends NroServerPacket {
    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        // TODO SEND CAUPTION

    }
}
