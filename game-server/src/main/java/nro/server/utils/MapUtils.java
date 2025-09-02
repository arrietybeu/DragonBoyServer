package nro.server.utils;

import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.artemis.utils.ImmutableBag;
import lombok.NoArgsConstructor;
import nro.server.engine.entity.GameWorld;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.model.map.GameMap;
import nro.server.model.map.GameMapFactory;
import nro.server.model.map.zone.Zone;
import nro.server.model.map.zone.ZoneManager;
import nro.server.model.map.zone.ZoneType;
import nro.server.model.map.zone.type.NormalZoneManager;
import nro.server.model.map.zone.type.OfflineZoneManager;
import nro.server.model.world.WorldPosition;


/**
 * @author Arriety
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class MapUtils {

    /**
     * Tìm/Join 1 zone trong map theo loại
     */
    public static Zone enterZone(short mapId, int playerId, Integer targetZoneId, boolean autoSwitchIfFull) {
        GameMap gm = GameMapFactory.getInstance().getMap(mapId);
        if (gm == null) throw new IllegalArgumentException("Map không tồn tại: " + mapId);

        ZoneManager zm = gm.zoneManager();
        ZoneType t = gm.type();

        return switch (t) {
            case NORMAL -> {
                var normal = (NormalZoneManager) zm;
                yield normal.joinNormalZone(playerId, targetZoneId, autoSwitchIfFull);
            }
            case OFFLINE -> {
                var offline = (OfflineZoneManager) zm;
                yield offline.joinOfflineZone(playerId);
            }
            case DUNGEON -> throw new UnsupportedOperationException("Phó bản chưa hỗ trợ");
            case EVENT -> throw new UnsupportedOperationException("Sự kiện chưa hỗ trợ");
        };
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
        if (map != null && map.zoneManager() instanceof NormalZoneManager n) {
            n.onPlayerLeave(zone);
        }
    }

    /**
     * Move entity giữa 2 zone
     */
    public static void move(Entity entity, Zone from, Zone to) {
        if (from != null) detachFromZone(entity, from);
        attachToZone(entity, to);
    }

    /**
     * Lấy toàn bộ entity trong group của zone
     */
    public static ImmutableBag<Entity> getEntities(Zone zone) {
        GroupManager gm = GameWorld.getInstance().getGroupManager();
        ImmutableBag<Entity> bag = gm.getEntities(zone.groupName());
        return bag != null ? bag : new com.artemis.utils.Bag<>();
    }

    /**
     * Đếm số player trong zone
     */
    public static int countPlayers(Zone zone) {
        var world = GameWorld.getInstance().getWorld();
        var mPlayer = world.getMapper(PlayerComponent.class);
        var bag = getEntities(zone);

        int c = 0;
        for (int i = 0; i < bag.size(); i++) {
            Entity e = bag.get(i);
            if (e != null && mPlayer.has(e)) c++;
        }
        return c;
    }

    /**
     * Tạo WorldPosition mới (chỉ đơn giản gắn zoneId vào)
     */
    public static WorldPosition createPosition(short mapId, int playerId, short x, short y,
                                               Integer targetZoneId, boolean autoSwitchIfFull) {
        Zone z = enterZone(mapId, playerId, targetZoneId, autoSwitchIfFull);
        return new WorldPosition(mapId, x, y, z.zoneId());
    }
}
