package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.network.nro.server_packets.handler.SmDialogMessage;

import java.util.Set;

/**
 * @author Arriety
 */

@AClientPacketHandler(command = ConstsCmd.LOGIN2, validStates = {NroConnection.State.CONNECTED})
public class CmLogin2 extends NroClientPacket {

    // TODO chưa làm chức này
    public CmLogin2(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        getConnection().sendPacket(new SmDialogMessage("deo co gi car"));
    }

    @Override
    protected void runImpl() {
    }
}
