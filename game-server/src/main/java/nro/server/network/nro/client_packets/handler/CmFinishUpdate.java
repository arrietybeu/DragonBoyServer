package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.services.player.PlayerEnterWorldService;

import java.util.Set;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.FINISH_UPDATE, validStates = {NroConnection.State.AUTHED})
public class CmFinishUpdate extends NroClientPacket {

    public CmFinishUpdate(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        if (getConnection().getSessionInfo().isLogin())
            throw new IllegalStateException("Cannot finish update while logged in - " + getConnection());
        PlayerEnterWorldService.enterWorld(getConnection());
    }

    @Override
    protected void runImpl() {
    }

}
