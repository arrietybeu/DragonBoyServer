package nro.server.services;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nro.server.network.nro.server_packets.handler.SmMobHp;

/**
 * @author Arriety
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MonsterService {

    public static void sendHpMonster(int mapId, int zoneId, int monsterId, long hp, long dame,
            boolean isCrit, boolean isHutHp) {

        var packet = new SmMobHp(monsterId, hp, dame, isCrit, isHutHp);

        AreaService.getInstance().sendPacketForALLPlayerInArea(mapId, zoneId, packet);
    }

}
