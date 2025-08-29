package nro.server.utils;

import com.artemis.Entity;
import lombok.NoArgsConstructor;
import nro.server.engine.entity.GameWorld;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.handler.SmChatTheGioi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author arriety
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class PacketSendUtility {

    private static final Logger log = LoggerFactory.getLogger(PacketSendUtility.class);

    public static void sendMessage(int entityId, String msg) {
        PlayerComponent playerComponent = null;
        try {
            playerComponent = GameWorld.getInstance().getWorld().getEntity(entityId).getComponent(PlayerComponent.class);
            if (playerComponent == null) throw new NullPointerException();

            sendPacket(playerComponent, new SmChatTheGioi(msg));
        } catch (Exception e) {
            log.error("Error sending message to entityId {}: client: {} error: {}", entityId, (playerComponent == null ? "null" : playerComponent.connection), e.getMessage(), e);
        }
    }

    public static void sendPacket(PlayerComponent player, NroServerPacket packet) {
        if (player.isOnline()) player.connection.sendPacket(packet);
    }

    public static void sendPacketForPlayersInZoneNotMe(Entity entity, NroServerPacket packet) {
        try {

        } catch (Exception e) {

        }
    }
}
