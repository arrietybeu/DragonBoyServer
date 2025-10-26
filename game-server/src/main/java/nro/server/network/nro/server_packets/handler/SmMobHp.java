package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.consts.ConstEffect;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.MOB_HP)
public class SmMobHp extends NroServerPacket {

    private final int monsterId;
    private final long hp;
    private final long dame;
    private final boolean isCrit;
    private final boolean isHutHp;

    public SmMobHp(int monsterId, long hp, long dame, boolean isCrit, boolean isHut) {
        this.monsterId = monsterId;
        this.hp = hp;
        this.dame = dame;
        this.isCrit = isCrit;
        this.isHutHp = isHut;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        writeInt(monsterId);
        writeLong(hp);
        writeLong(dame);
        writeBoolean(isCrit);
        writeByte(isHutHp ? ConstEffect.EFFECT_HUT_MAU : ConstEffect.DEFAULT);
    }
}
