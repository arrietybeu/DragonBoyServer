package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.network.nro.server_packets.handler.SmOpenUiZone;

import java.util.Set;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.OPEN_UI_ZONE, validStates = {NroConnection.State.IN_GAME})
public class CmOpenUIZone extends NroClientPacket {

    public CmOpenUIZone(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        // nothing to read

        // TODO có thể check kỹ thêm laf entity co active khong co online khong
    }

    @Override
    protected void runImpl() {
        getConnection().sendPacket(new SmOpenUiZone());
    }

}
