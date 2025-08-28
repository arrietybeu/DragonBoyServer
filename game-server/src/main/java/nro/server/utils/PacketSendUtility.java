package nro.server.utils;

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
        var playerComponent = GameWorld.getInstance().getWorld().getEntity(entityId).getComponent(PlayerComponent.class);
        if (playerComponent == null) throw new NullPointerException();

        sendPacket(playerComponent, new SmChatTheGioi(msg));
    }

    public static void sendPacket(PlayerComponent player, NroServerPacket packet) {
        if (player.isOnline()) player.connection.sendPacket(packet);
    }

}
