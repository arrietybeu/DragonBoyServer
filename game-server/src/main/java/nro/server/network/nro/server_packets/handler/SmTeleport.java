package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.TELEPORT)
public class SmTeleport extends NroServerPacket {

    private final int playerId;

    public SmTeleport(int playerId) {
        this.playerId = playerId;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        var entity = con.getEntity();

        var pos = entity.getComponent(PositionComponent.class);

        writeInt(playerId);
        writeByte(pos.teleport);
    }
}
