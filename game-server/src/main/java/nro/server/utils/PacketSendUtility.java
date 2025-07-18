package nro.server.utils;

import nro.server.engine.GameWorld;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.handler.SmChatTheGioi;

/**
 * @author arriety
 */
public class PacketSendUtility {

    public static void sendMessage(int entityId, String msg) {
        var playerComponent = GameWorld.getInstance().getWorld().getEntity(entityId).getComponent(PlayerComponent.class);
        if (playerComponent == null)
            throw new NullPointerException();

        sendPacket(playerComponent, new SmChatTheGioi(msg));
    }

    public static void sendPacket(PlayerComponent player, NroServerPacket packet) {
        if (player.isOnline())
            player.connection.sendPacket(packet);
    }
}
