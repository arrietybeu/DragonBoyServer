package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.ME_LOAD_POINT)
public class SmMeLoadPoint extends NroServerPacket {

    private final int baseHp, baseMp, baseDamage, baseDefense;
    private final long maxHp, maxMp, currentHp, currentMp, currentDamage, totalDefense, potential;
    private final byte movementSpeed, hpPer1000, mpPer1000, damagePer1000, baseCrit, totalCrit;
    private final short expPerStat;

    public SmMeLoadPoint(int baseHp, int baseMp, int baseDamage, long maxHp,
                         long maxMp, long currentHp, long currentMp, byte movementSpeed,
                         byte hpPer1000, byte mpPer1000, byte damagePer1000, long currentDamage,
                         long totalDefense, byte totalCrit, long potential, short expPerStat,
                         int baseDefense, byte baseCrit) {
        this.baseHp = baseHp;
        this.baseMp = baseMp;
        this.baseDamage = baseDamage;
        this.maxHp = maxHp;
        this.maxMp = maxMp;
        this.currentHp = currentHp;
        this.currentMp = currentMp;
        this.movementSpeed = movementSpeed;
        this.hpPer1000 = hpPer1000;
        this.mpPer1000 = mpPer1000;
        this.damagePer1000 = damagePer1000;
        this.currentDamage = currentDamage;
        this.totalDefense = totalDefense;
        this.totalCrit = totalCrit;
        this.potential = potential;
        this.expPerStat = expPerStat;
        this.baseDefense = baseDefense;
        this.baseCrit = baseCrit;
    }

    @Override
    protected void writeImpl(NroConnection con) {
        writeInt(baseHp);
        writeInt(baseMp);
        writeInt(baseDamage);
        writeLong(maxHp);
        writeLong(maxMp);
        writeLong(currentHp);
        writeLong(currentMp);
        writeByte(movementSpeed);
        writeByte(hpPer1000);
        writeByte(mpPer1000);
        writeByte(damagePer1000);
        writeLong(currentDamage);
        writeLong(totalDefense);
        writeByte(totalCrit);
        writeLong(potential);
        writeShort(expPerStat);
        writeInt(baseDefense);
        writeByte(baseCrit);
    }
}