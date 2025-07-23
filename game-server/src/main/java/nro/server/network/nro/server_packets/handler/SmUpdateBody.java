package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.model.ecs.component.AppearanceComponent;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.UPDATE_BODY)
public class SmUpdateBody extends NroServerPacket {

    private final int playerId;
    private final AppearanceComponent appearance;

    public SmUpdateBody(final int playerId, final AppearanceComponent appearance) {
        this.playerId = playerId;
        this.appearance = appearance;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        writeByte(1);
        writeInt(playerId);
        writeShort(appearance.head);
        writeShort(appearance.body);
        writeShort(appearance.leg);
        System.out.println("write UPDATABE BODY: " + playerId + " head: " + appearance.head + " body: " + appearance.body + " leg: " + appearance.leg);
        writeByte(appearance.isMonkey ? 1 : 0);
    }

}
