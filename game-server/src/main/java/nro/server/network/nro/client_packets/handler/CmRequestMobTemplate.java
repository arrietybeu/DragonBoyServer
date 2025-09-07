package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.network.nro.server_packets.handler.SmRequestMobTemplate;

import java.util.Set;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.REQUEST_MOB_TEMPLATE, validStates = {NroConnection.State.IN_GAME})
public class CmRequestMobTemplate extends NroClientPacket {

    private short id;

    public CmRequestMobTemplate(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        id = readShort();
    }

    @Override
    protected void runImpl() {
        getClient().sendPacket(new SmRequestMobTemplate(id));
    }
}
