package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.network.nro.server_packets.PacketHelper;
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

    }

    @Override
    protected void runImpl() {

        // if the player is null show the character creation form
        sendPacket(PacketHelper.empty(ConstsCmd.CLIENT_INFO));

        // else send a packet to enter the world.

        PlayerEnterWorldService.enterWorld(getConnection());
    }

}
