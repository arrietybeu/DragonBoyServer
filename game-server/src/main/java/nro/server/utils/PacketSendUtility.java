package nro.server.utils;

import com.artemis.Entity;
import nro.server.engine.GameWorld;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.model.templates.world.WorldMapTemplate;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.handler.SmChatTheGioi;
import nro.server.world.World;

import java.io.IOException;

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

    public static void sendPacketForALLPlayerInArea(int mapID, int areaID, NroServerPacket packet) {

        var area = World.getInstance().getAreaInMap(mapID, areaID);

        if (area == null)
            throw new IllegalArgumentException("No area found for map ID " + mapID + " and area ID " + areaID);

        var entities = area.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);

            if (e == null) continue;

            var playerComponent = e.getComponent(PlayerComponent.class);

            if (playerComponent != null && playerComponent.isOnline()) {
                playerComponent.connection.sendPacket(packet);
            }
        }
    }

    public static void writeMapInfo(NroServerPacket packet, WorldMapTemplate mapTemplate, PositionComponent position) throws IOException {
        // Write player position
        packet.writeShort(position.x);
        packet.writeShort(position.y);

        // Write waypoints
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
        packet.writeByte(0); // NPC count

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
