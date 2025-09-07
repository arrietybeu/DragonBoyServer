package nro.server.network.nro.server_packets;

import com.artemis.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nro.server.engine.GameWorld;
import nro.server.model.ecs.component.*;
import nro.server.model.ecs.component.item.ItemInfoComponent;
import nro.server.model.ecs.component.item.ItemStatsComponent;
import nro.server.model.ecs.component.monster.InfoMonsterComponent;
import nro.server.model.ecs.component.monster.StastMonsterComponent;
import nro.server.model.ecs.component.monster.StateMonsterComponent;
import nro.server.model.map.GameMap;
import nro.server.model.map.GameMapFactory;
import nro.server.model.map.zone.Zone;
import nro.server.model.templates.world.WorldMapTemplate;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.utils.MapUtils;

import java.util.List;

/**
 * @author Arriety
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

    public static void writeMapInfo(NroServerPacket packet, WorldMapTemplate mapTemplate, PositionComponent position) {

        GameMap map = GameMapFactory.getInstance().getMap(position.mapId);
        Zone zone = MapUtils.findZone(map.id(), position.getAreaId());

        var w = GameWorld.getInstance().getWorld();
        var mI = w.getMapper(InfoMonsterComponent.class);
        var mS = w.getMapper(StastMonsterComponent.class);
        var st = w.getMapper(StateMonsterComponent.class);
        var mP = w.getMapper(PositionComponent.class);

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

        var monsterInZone = MapUtils.getMonsters(zone);
        packet.writeByte(monsterInZone.size());
        for (int i = 0, n = monsterInZone.size(); i < n; i++) {
            var monster = monsterInZone.get(i);
            var info = mI.get(monster);
            var stats = mS.get(monster);
            var state = st.get(monster);

            var pos = mP.get(monster);

            packet.writeInt(monster.getId());
            packet.writeBoolean(false);          // isDisable
            packet.writeBoolean(false);          // dontMove
            packet.writeBoolean(false);          // fire
            packet.writeBoolean(false);          // ice
            packet.writeBoolean(false);          // wind
            packet.writeShort((short) info.templateID);
            packet.writeByte(0);                 // sys
            packet.writeLong(stats.hp > 0 ? stats.hp : stats.hpMax);
            packet.writeByte(stats.level);
            packet.writeLong(stats.hpMax);
            packet.writeShort(pos.x);
            packet.writeShort(pos.y);
            packet.writeByte(state.status);
            packet.writeByte(stats.levelBoss);
            packet.writeBoolean(stats.isBoss);
        }

        packet.writeByte(0); // Monster extra data

        // Write NPC data (currently empty)
        var npcs = map.npcs();
        packet.writeByte(npcs.size());
        for (var npc : npcs) {
            if (npc == null)
                continue;

            packet.writeByte(npc.status());
            packet.writeShort(npc.x());
            packet.writeShort(npc.y());
            packet.writeByte(npc.id());
            packet.writeShort(npc.avatarId());

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
