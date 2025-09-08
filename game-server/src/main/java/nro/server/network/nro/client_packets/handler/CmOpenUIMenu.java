package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.services.NpcService;

import java.util.Set;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.OPEN_UI_MENU, validStates = { NroConnection.State.IN_GAME })
public class CmOpenUIMenu extends NroClientPacket {

    private short npcId;

    public CmOpenUIMenu(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        npcId = this.readShort();
    }

    @Override
    protected void runImpl() {
        NpcService.getInstance().openMenuNpc(getConnection(), npcId);
    }

}
