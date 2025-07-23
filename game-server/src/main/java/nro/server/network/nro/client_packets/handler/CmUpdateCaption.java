package nro.server.network.nro.client_packets.handler;

import java.util.Set;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroConnection.State;
import nro.server.network.nro.client_packets.AClientPacketHandler;

/**
 * @author Arriety
 */

@AClientPacketHandler(command = ConstsCmd.UPDATE_CAPTION, validStates = { NroConnection.State.IN_GAME })
public class CmUpdateCaption extends NroClientPacket {

    public CmUpdateCaption(int command, Set<State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        var gender = readByte();
    }

    @Override
    protected void runImpl() {
    }

}
