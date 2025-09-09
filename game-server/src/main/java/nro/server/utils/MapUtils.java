package nro.server.utils;

import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nro.server.data_holders.repo.MapData;
import nro.server.engine.GameWorld;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.ecs.component.monster.InfoMonsterComponent;
import nro.server.model.ecs.component.monster.StastMonsterComponent;
import nro.server.model.ecs.component.monster.StateMonsterComponent;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.model.map.GameMap;
import nro.server.model.map.GameMapFactory;
import nro.server.model.map.MapPosition;
import nro.server.model.map.zone.Zone;
import nro.server.model.map.zone.type.NormalZoneManager;
import nro.server.model.npc.Npc;
import nro.server.model.templates.world.Waypoint;

import java.util.Collection;

/**
 * @author Arriety
 */
@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class MapUtils {

    public static Zone findZone(short mapId, int zoneId) {
        GameMap gm = GameMapFactory.getInstance().getMap(mapId);
        if (gm == null)
            throw new IllegalArgumentException("Map không tồn tại: " + mapId);
        return gm.zoneManager().findZone(zoneId).orElse(null);
    }

    public static Collection<Zone> getAllZoneForMapID(short mapId) {
        GameMap gm = GameMapFactory.getInstance().getMap(mapId);
        if (gm == null)
            throw new IllegalArgumentException("Map không tồn tại: " + mapId);
        return gm.zoneManager().getZonesReadOnly();
    }

    public static void enterAndAttachToZone(short mapId, int entityID, Entity entity) {
        Zone zone = enterZone(mapId, entityID);
        attachToZone(entity, zone);
    }

    /**
     * Tìm/Join 1 zone trong map theo loại
     */
    public static Zone enterZone(short mapId, int entityID) {
        GameMap gm = GameMapFactory.getInstance().getMap(mapId);
        if (gm == null)
            throw new IllegalArgumentException("Map không tồn tại: " + mapId);
        return gm.zoneManager().joinZone(entityID);
    }

    /**
     * Add entity vào group của zone + tăng đếm
     */
    public static void attachToZone(Entity entity, Zone zone) {
        GroupManager gm = GameWorld.getInstance().getGroupManager();
        gm.add(entity, zone.groupName());
        zone.onPlayerJoin();

        var map = GameMapFactory.getInstance().getMap(zone.mapId());
        if (map != null && map.zoneManager() instanceof NormalZoneManager n) {
            n.onPlayerJoin(zone);
        }
    }

    /**
     * Remove entity khỏi group của zone + giảm đếm
     */
    public static void detachFromZone(Entity entity, Zone zone) {
        GroupManager gm = GameWorld.getInstance().getGroupManager();
        gm.remove(entity, zone.groupName());
        zone.onPlayerLeave();

        var map = GameMapFactory.getInstance().getMap(zone.mapId());

        if (map == null) {
            throw new NullPointerException(
                    "detachFromZone map not found for map ID: " + zone.mapId() + " zone ID: " + zone.zoneId());
        }

        if (map.zoneManager() instanceof NormalZoneManager n) {
            n.onPlayerLeave(zone);
        }
    }

    /**
     * Move entity giữa 2 zone
     */
    public static void move(Entity entity, Zone from, Zone to) {
        if (from != null)
            detachFromZone(entity, from);
        attachToZone(entity, to);
    }

    /**
     * Lấy toàn bộ entity trong group của zone
     */
    public static ImmutableBag<Entity> getEntities(Zone zone) {
        GroupManager gm = GameWorld.getInstance().getGroupManager();
        ImmutableBag<Entity> bag = gm.getEntities(zone.groupName());
        return bag != null ? bag : new Bag<>();
    }

    public static ImmutableBag<Entity> getPlayers(Zone zone) {
        GroupManager gm = GameWorld.getInstance().getGroupManager();
        ImmutableBag<Entity> bag = gm.getEntities(zone.groupName());
        if (bag == null || bag.isEmpty())
            return new Bag<>();

        var world = GameWorld.getInstance().getWorld();
        var mPlayer = world.getMapper(PlayerComponent.class);

        var out = new Bag<Entity>(bag.size());
        for (int i = 0; i < bag.size(); i++) {
            Entity e = bag.get(i);
            if (e != null && mPlayer.has(e))
                out.add(e);
        }
        return out;
    }

    /**
     * Đếm số player trong zone
     */
    public static int countPlayers(Zone zone) {
        var world = GameWorld.getInstance().getWorld();
        var mPlayer = world.getMapper(PlayerComponent.class);
        var bag = getPlayers(zone);

        int c = 0;
        for (int i = 0; i < bag.size(); i++) {
            Entity e = bag.get(i);
            if (e != null && mPlayer.has(e))
                c++;
        }
        return c;
    }

    /**
     * Tạo WorldPosition mới (chỉ đơn giản gắn zoneId vào)
     */
    public static MapPosition createPosition(short mapId, int entityID, short x, short y) {
        Zone z = enterZone(mapId, entityID);
        return new MapPosition(mapId, x, y, z.zoneId());
    }

    public static Waypoint getWayPointInMap(int mapID, int x, int y, int playerID) {

        var template = MapData.getInstance().getWorldMapTemplate(mapID);
        try {
            if (mapID == 46) {
                int delta = 1000;
                var sub = template.getWaypointMap().subMap(x - delta, true, x + delta, true);

                for (var list : sub.values()) {
                    for (Waypoint wp : list) {
                        if (x >= wp.getMinX() - delta && x <= wp.getMaxX() + delta && y >= wp.getMinY()
                                && y <= wp.getMaxY()) {
                            return wp;
                        }
                    }
                }
            } else {
                var entry = template.getWaypointMap().floorEntry(x);
                if (entry != null) {
                    for (Waypoint wp : entry.getValue()) {
                        // FIXME tại client msg -7 t xử lý msg ngu quá nó cứ set y = 0 nên là lỗi không
                        // tìm thấy waypoint thôi thì tạm thời đóng chức năng check y nhé =)))
                        if (x >= wp.getMinX() && x <= wp.getMaxX() /* && y >= wp.getMinY() && y <= wp.getMaxY() */) {
                            return wp;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error getting waypoint for player: " + playerID + " x: " + x + "-y: " + y, ex);
        }
        return null;
    }

    public static Npc getNpcByIdForMap(GameMap map, int id) {
        for (var npc : map.npcs()) {
            if (npc.id() == id)
                return npc;
        }
        return null;
    }

    public static void attachMonsterToZone(Entity entity, Zone zone) {
        GroupManager gm = GameWorld.getInstance().getGroupManager();
        gm.add(entity, zone.groupName()); // group chung của zone
        gm.add(entity, monsterGroupName(zone));// group phụ riêng cho monster
    }

    public static void detachMonsterFromZone(Entity entity, Zone zone) {
        GroupManager gm = GameWorld.getInstance().getGroupManager();
        gm.remove(entity, zone.groupName());
        gm.remove(entity, monsterGroupName(zone));
    }

    public static ImmutableBag<Entity> getMonsters(Zone zone) {
        GroupManager gm = GameWorld.getInstance().getGroupManager();
        ImmutableBag<Entity> bag = gm.getEntities(monsterGroupName(zone));
        return bag != null ? bag : new com.artemis.utils.Bag<>();
    }

    public static void spawnMonster(short mapId,
            int zoneId,
            int templateId,
            String name,
            short x, short y,
            byte level,
            long hpMax) {

        Zone z = findZone(mapId, zoneId);
        if (z == null)
            throw new IllegalArgumentException("Zone không tồn tại: map=" + mapId + ", zone=" + zoneId);

        var world = GameWorld.getInstance().getWorld();
        Entity e = world.createEntity();

        var info = new InfoMonsterComponent(templateId, name);
        var position = new PositionComponent(mapId, x, y, z.zoneId());
        var stats = new StastMonsterComponent(hpMax, level);
        var state = new StateMonsterComponent();

        e.edit().add(info);
        e.edit().add(stats);
        e.edit().add(position);
        e.edit().add(state);

        attachMonsterToZone(e, z);
    }

    private static String monsterGroupName(Zone zone) {
        return zone.groupName() + ":monster";
    }

}
