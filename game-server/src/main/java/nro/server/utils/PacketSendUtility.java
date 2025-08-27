package nro.server.utils;

import lombok.NoArgsConstructor;
import nro.server.engine.entity.GameWorld;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.handler.SmChatTheGioi;
import nro.server.model.world.WorldMapInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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

    public static void writeMapInfo(NroServerPacket packet, WorldMapInstance zone, PositionComponent position) throws IOException {
        // Write player position
        packet.writeShort(position.x);
        packet.writeShort(position.y);

        // Write waypoints
        var mapTemplate = zone.getParent().getTemplate();
        var wayPoints = mapTemplate.getWaypoints();
        packet.writeByte(wayPoints.size());
        for (var wayPoint : wayPoints) {
            packet.writeShort(wayPoint.getMinX());
            packet.writeShort(wayPoint.getMinY());
            packet.writeShort(wayPoint.getMaxX());
            packet.writeShort(wayPoint.getMaxY());
            packet.writeBoolean(wayPoint.isEnter());
            packet.writeBoolean(wayPoint.isOffline());
            packet.writeUTF(wayPoint.getName());
        }

        // Write monster data (currently empty)
        packet.writeByte(0); // Monster count
        packet.writeByte(0); // Monster extra data

        // Write NPC data (currently empty)
        var npcs = zone.getParent().getNpcs();
        packet.writeByte(npcs.size());
        for (var npc : npcs) {
            if (npc != null) {
                packet.writeByte(npc.status());
                packet.writeShort(npc.x());
                packet.writeShort(npc.y());
                packet.writeByte(npc.id());
                packet.writeShort(npc.avatarId());
            } else {
                log.warn("NPC is null in Map {}", mapTemplate);
            }
        }

        // Write item map data (currently empty)
        packet.writeByte(0); // Item map count

        // Write background items
        var bgItems = mapTemplate.getBgItems();
        packet.writeShort(bgItems.size());
        for (var bgItem : bgItems) {
            packet.writeShort(bgItem.getId());
            packet.writeShort(bgItem.getX());
            packet.writeShort(bgItem.getY());
        }

        // Write background effects
        var backgroundEffects = mapTemplate.getBackgroundEffects();
        packet.writeShort(backgroundEffects.size());
        for (var effect : backgroundEffects) {
            packet.writeUTF(effect.key());
            packet.writeUTF(effect.value());
        }

        // Write background type and teleport flag
        packet.writeByte(mapTemplate.getBgType());
        packet.writeByte(position.teleport);

        // Reset teleport flag
        position.teleport = 0;
    }
}
