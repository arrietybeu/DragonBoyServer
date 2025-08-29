package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;

import java.util.Set;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.CHANGE_FLAG, validStates = {NroConnection.State.IN_GAME})
public class CmChangeFlag extends NroClientPacket {

    private byte action;
    private byte index;

    public CmChangeFlag(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        action = readByte();
        index = -1;
        if (action != 0) index = readByte();
    }

    @Override
    protected void runImpl() {
    }
}
