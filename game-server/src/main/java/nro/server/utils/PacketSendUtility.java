package nro.server.utils;

import com.artemis.Entity;
import lombok.NoArgsConstructor;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.network.nro.NroServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author arriety
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class PacketSendUtility {

    private static final Logger log = LoggerFactory.getLogger(PacketSendUtility.class);

    public static void sendPacket(PlayerComponent player, NroServerPacket packet) {
        if (player.isOnline()) player.connection.sendPacket(packet);
    }

    public static void sendPacketForPlayersInZoneNotMe(Entity entity, NroServerPacket packet) {
        try {

        } catch (Exception e) {

        }
    }
}
