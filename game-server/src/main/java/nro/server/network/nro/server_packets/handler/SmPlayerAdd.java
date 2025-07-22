package nro.server.network.nro.server_packets.handler;

import com.artemis.Entity;
import nro.commons.consts.ConstsCmd;
import nro.server.model.ecs.component.*;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
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


        if (this.writePlayerInfo(playerInZone, state, position, appearance)) {
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

    private boolean writePlayerInfo(Entity entity, StateComponent state, PositionComponent position, AppearanceComponent appearance) {
        var buff = entity.getComponent(BuffComponent.class);
        var info = entity.getComponent(InfoComponent.class);
        var heal = entity.getComponent(HealthComponent.class);

        writeByte(1);// level
        writeBoolean(false); // write isInvisiblez
        writeByte(state.typePk);
        writeByte(info.gender);
        writeByte(info.gender);
        writeShort(appearance.head);
        writeUTF(info.name);
        writeLong(heal.currentHP);
        writeLong(heal.currentMP);
        writeShort(appearance.body);
        writeShort(appearance.leg);
        writeShort(appearance.flagBag);
        writeByte(0);
        writeShort(position.x);
        writeShort(position.y);
        writeShort(buff.eff5BuffHp);
        writeShort(buff.eff5BuffMp);
        writeByte(0);
        return true;
    }

}
