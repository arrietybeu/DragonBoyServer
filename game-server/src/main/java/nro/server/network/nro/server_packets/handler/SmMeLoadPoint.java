package nro.server.network.nro.server_packets.handler;

import com.artemis.Entity;
import nro.commons.consts.ConstsCmd;
import nro.server.model.ecs.component.HealthComponent;
import nro.server.model.ecs.component.StatsComponent;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.ME_LOAD_POINT)
public class SmMeLoadPoint extends NroServerPacket {

    @Override
    protected void writeImpl(NroConnection con) {
        Entity playerEntity = con.getEntity();
        if (playerEntity == null) {
            throw new RuntimeException("Player entity not found: " + con);
        }
        StatsComponent stats = playerEntity.getComponent(StatsComponent.class);
        HealthComponent health = playerEntity.getComponent(HealthComponent.class);
        if (stats == null) {
            throw new RuntimeException("Player entity does not have StatsComponent: " + con);
        }
        if (health == null) {
            throw new RuntimeException("Player entity does not have HealthComponent: " + con);
        }

        writeInt(stats.baseHp);
        writeInt(stats.baseMp);
        writeInt(stats.baseDamage);
        writeLong(health.maxHP);
        writeLong(health.maxMP);
        writeLong(health.currentHP);
        writeLong(health.currentMP);
        writeByte(stats.movementSpeed);
        writeByte(health.hpPer1000Potential);
        writeByte(health.mpPer1000Potential);
        writeByte(health.damagePer1000Potential);
        writeLong(stats.currentDamage);
        writeLong(stats.totalDefense);
        writeByte(stats.totalCriticalChance);
        writeLong(stats.potential);
        writeShort(stats.expPerStatIncrease);
        writeInt(stats.baseDefense);
        writeByte(stats.baseCrit);
        writeByte(stats.giamST);
        writeShort(stats.cCritDameFull);

        // log toàn bộ info đã write
        System.out.println("SmMeLoadPoint: " + stats + ", Health: " + health);
    }
}