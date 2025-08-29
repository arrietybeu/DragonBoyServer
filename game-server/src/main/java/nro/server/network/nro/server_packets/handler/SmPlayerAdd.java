package nro.server.network.nro.server_packets.handler;

import com.artemis.Entity;
import nro.commons.consts.ConstsCmd;
import nro.server.model.ecs.component.*;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.PacketHelper;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.PLAYER_ADD)
public class SmPlayerAdd extends NroServerPacket {

    private final Entity playerInZone;

    public SmPlayerAdd(Entity playerInZone) {
        this.playerInZone = playerInZone;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        if (playerInZone == null)
            throw new RuntimeException("playerEntity is null!!!");

        var appearance = playerInZone.getComponent(AppearanceComponent.class);
        var position = playerInZone.getComponent(PositionComponent.class);
        var state = playerInZone.getComponent(StateComponent.class);
        var fusion = playerInZone.getComponent(FusionComponent.class);
        writeInt(con.getPlayerID());
        writeInt(-1);// clan id

        if (PacketHelper.writePlayerInfo(this, playerInZone, state, position, appearance)) {
            writeByte(position.teleport);
            writeByte(appearance.isMonkey ? 1 : 0);
            writeShort(appearance.mount);
        }

        writeByte(state.pkFlag);
        writeByte(fusion.fusionType);

        writeShort(appearance.aura);
        writeShort(appearance.effSetItem);
        writeShort(appearance.idHat);
    }

}
