package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.model.ecs.component.AppearanceComponent;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.UPDATE_BAG)
public class SmUpdateBag extends NroServerPacket {

    private final AppearanceComponent appearanceComponent;
    private final int playerID;

    public SmUpdateBag(final int entityId, final AppearanceComponent appearanceComponent) {
        this.appearanceComponent = appearanceComponent;
        this.playerID = entityId;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        writeInt(playerID);
        writeShort(appearanceComponent.flagBag);
    }
}
