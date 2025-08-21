package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.network.nro.server_packets.handler.SmDialogMessage;
import nro.server.services.player.PlayerEnterWorldService;

import java.util.Set;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.FINISH_UPDATE, validStates = {NroConnection.State.AUTHED,
        NroConnection.State.IN_GAME})

public class CmFinishUpdate extends NroClientPacket {

    public CmFinishUpdate(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        if (getConnection().getSessionInfo().isLogin()) {
            this.getConnection().close(new SmDialogMessage("Vui lòng đăng nhập lại!"));
            return;
        }
        PlayerEnterWorldService.enterWorld(getConnection());
        getConnection().getSessionInfo().setLogin(true);
    }

    @Override
    protected void runImpl() {
    }

}
