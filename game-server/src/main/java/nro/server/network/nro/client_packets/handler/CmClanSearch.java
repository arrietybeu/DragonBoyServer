package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.network.nro.server_packets.PacketHelper;

import java.util.Set;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.CLAN_SEARCH, validStates = { NroConnection.State.IN_GAME })
public class CmClanSearch extends NroClientPacket {

    private String name;

    public CmClanSearch(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        this.name = this.readUTF();
    }

    @Override
    protected void runImpl() {
        if (name.isEmpty()) {
            // show 20 list clan ngau nhien
        } else {
            // show list clan co ten chua name
        }

        PacketHelper.hideDia();
    }

}
