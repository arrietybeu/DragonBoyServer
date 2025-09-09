package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;

import java.util.Set;

@AClientPacketHandler(command = ConstsCmd.CHECK_MOVE, validStates = { NroConnection.State.IN_GAME })
public class CmCheckMove extends NroClientPacket {

    private int second;

    // FIXME chuaw biet msg nay de lam gi
    public CmCheckMove(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        this.second = this.readInt();
    }

    @Override
    protected void runImpl() {
        log.debug("client check move for second: " + second);
    }
}
