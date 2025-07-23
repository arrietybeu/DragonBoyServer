package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.network.nro.server_packets.handler.SmRequestIcon;

import java.util.Set;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.REQUEST_ICON, validStates = {NroConnection.State.IN_GAME})
public class CmRequestIcon extends NroClientPacket {
    public CmRequestIcon(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        var id = readInt();
        getConnection().sendPacket(new SmRequestIcon(id));
    }

    @Override
    protected void runImpl() {

    }
}
