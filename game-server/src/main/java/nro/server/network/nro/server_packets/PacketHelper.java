package nro.server.network.nro.server_packets;

import com.artemis.Entity;
import nro.server.engine.entity.GameWorld;
import nro.server.model.ecs.component.*;
import nro.server.model.ecs.component.item.ItemInfoComponent;
import nro.server.model.ecs.component.item.ItemStatsComponent;
import nro.server.model.world.WorldMapInstance;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;

import java.util.List;


/**
 * @author Arriety
 */
public final class PacketHelper {

    public static NroServerPacket empty(int opcode) {
        return new NroServerPacket(opcode) {
            @Override
            protected void writeImpl(NroConnection con) {
            }
        };
    }

    public static void sendInventoryForPlayer(NroServerPacket data, List<Integer> items) {
        data.writeByte(items.size());
        for (var item : items) {
            if (item == -1) {
                data.writeShort(-1);
                continue;
            }

            var world = GameWorld.getInstance().getWorld().getEntity(item);

            var itemInfo = world.getComponent(ItemInfoComponent.class);

            data.writeShort(itemInfo.templateId);
            data.writeInt(itemInfo.quantity);
            data.writeUTF("");
            data.writeUTF("");

            var itemStats = world.getComponent(ItemStatsComponent.class);

            writeDataOptions(data, itemStats);
        }
    }

    private static void writeDataOptions(NroServerPacket packet, ItemStatsComponent statsComponent) {
        if (statsComponent == null || statsComponent.options.isEmpty()) {
            packet.writeByte(1);
            packet.writeShort(73);
            packet.writeInt(0);
        } else {
            packet.writeByte(statsComponent.options.size());
            for (var option : statsComponent.options) {
                packet.writeShort(option.id());
                packet.writeInt(option.param());
            }
        }
    }

    public static void writeMapInfo(NroServerPacket packet, WorldMapInstance zone, PositionComponent position) {
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
                throw new RuntimeException("NPC is null in Map: " + mapTemplate);
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

    public static boolean writePlayerInfo(NroServerPacket packet, Entity entity, InfoComponent info, StateComponent state, PositionComponent position, AppearanceComponent appearance) {
        var buff = entity.getComponent(BuffComponent.class);
        var heal = entity.getComponent(HealthComponent.class);

        packet.writeByte(1);// level
        packet.writeBoolean(false); // write isInvisiblez
        packet.writeByte(state.typePk);
        packet.writeByte(info.gender);
        packet.writeByte(info.gender);
        packet.writeShort(appearance.head);
        packet.writeUTF(info.name);
        packet.writeLong(heal.currentHP);
        packet.writeLong(heal.currentMP);
        packet.writeShort(appearance.body);
        packet.writeShort(appearance.leg);
        packet.writeShort(appearance.flagBag);
        packet.writeByte(0);
        packet.writeShort(position.x);
        packet.writeShort(position.y);
        packet.writeShort(buff.eff5BuffHp);
        packet.writeShort(buff.eff5BuffMp);
        packet.writeByte(0);
        return true;
    }
}
