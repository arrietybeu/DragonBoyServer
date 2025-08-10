package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;

import java.util.Set;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.OPEN_UI_MENU, validStates = {NroConnection.State.IN_GAME})
public class CmOpenUIMenu extends NroClientPacket {

    public CmOpenUIMenu(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
    }
}
