package nro.server.engine.base;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import nro.commons.consts.ConstsCmd;
import nro.server.model.ecs.component.*;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.model.map.GameMap;
import nro.server.model.map.GameMapFactory;
import nro.server.model.map.zone.Zone;
import nro.server.model.templates.world.Waypoint;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.server_packets.PacketHelper;
import nro.server.network.nro.server_packets.handler.SmMapInfo;
import nro.server.network.nro.server_packets.handler.SmResetPoint;
import nro.server.services.AreaService;
import nro.server.services.NotifyService;
import nro.server.utils.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arriety
 */
public final class MapChangeSystem extends IteratingSystem {

    private static final Logger log = LoggerFactory.getLogger(MapChangeSystem.class);

    private ComponentMapper<PositionComponent> posMapper;
    private ComponentMapper<InfoComponent> infoMapper;
    private ComponentMapper<PlayerComponent> players;

    public MapChangeSystem() {
        super(Aspect.all(PositionComponent.class, InfoComponent.class, PlayerComponent.class));
    }

    @Override
    protected void process(int entityId) {
        var pos = posMapper.get(entityId);

        if (!pos.wantsToChangeMap || pos.changeType == null) return;

        var info = infoMapper.get(entityId);
        var player = players.get(entityId);
        var client = (player != null) ? player.connection : null;

        try {
            switch (pos.changeType) {
                case WAYPOINT -> viaWaypoint(pos, info, client);
//                case SHIP -> viaShip(entityId, pos, info, client);
//                case ZONE -> viaZone(entityId, pos, client);
//                case TELEPORT -> viaTeleport(entityId, pos, client);
            }
        } catch (Throwable t) {
            if (client != null) {
                NotifyService.SendNotifyPlayer(client, "Có lỗi khi đổi map, đã đưa bạn về khu an toàn.");
                keepInSafeZone(null, client, pos);
            }
            log.error("MapChangeSystem error eId={}, player={} mapId={}", entityId, info, pos.mapId, t);
        } finally {
            pos.wantsToChangeMap = false;
            pos.changeType = null;
        }
    }

    private void viaWaypoint(PositionComponent pos, InfoComponent info, NroConnection con) {
        GameMap map = GameMapFactory.getInstance().getMap(pos.mapId);

        if (map == null) {
            keepInSafeZone(null, con, pos);
            throw new RuntimeException("Current map is null for entity " + info + " with mapId " + pos.mapId);
        }

        Waypoint wp = MapUtils.getWayPointInMap(pos.mapId, pos.x, pos.y, info.id);
        if (wp == null) {
            NotifyService.SendNotifyPlayer(con, "Bạn không thể đi đến đây!");
            keepInSafeZone(null, con, pos);
            throw new RuntimeException("Waypoint is null for entity " + info + " with mapId " + pos.mapId + " at position " + pos.x + ", " + pos.y + " in area " + pos.getAreaId());
        }

        moveToMapAutoZone(pos, con, (short) wp.getGoMap(), wp.getGoX(), wp.getGoY());
    }

    private void moveToMapAutoZone(PositionComponent pos, NroConnection client, short toMapId, short toX, short toY) {
        var e = client.getEntity();

        Zone from = MapUtils.findZone(pos.mapId, pos.getAreaId());
        Zone to = MapUtils.enterZone(toMapId, e.getId());
        var entities = MapUtils.getPlayers(from);

        if (to == null) {
            keepInSafeZone(null, client, pos);
            NotifyService.SendNotifyPlayer(client, "Khu vực này không có người quản lý, bạn không thể đi đến đây!");
            return;
        }

        // detach -> attach
        MapUtils.move(e, from, to);

        AreaService.getInstance().sendPacketPlayerExitArea(client, entities);

        // update vị trí
        pos.mapId = toMapId;
        pos.setAreaId(to.zoneId());

        pos.x = toX;
        pos.y = toY;
        AreaService.getInstance().sendMyInfoToPlayersInZone(client);
        sendMapChangePackets(client, pos);
    }

    private void sendMapChangePackets(NroConnection client, PositionComponent pos) {
        if (client == null) return;
        client.sendPacket(PacketHelper.empty(ConstsCmd.MAP_CLEAR));
        client.sendPacket(new SmMapInfo(pos));
    }

    private void keepInSafeZone(Waypoint waypoint, NroConnection con, PositionComponent pos) {
        if (pos == null) return;

        short safeX;
        short safeY = pos.y;

        if (waypoint == null) {
            safeX = 120;
            safeY = 336;
        } else {
            safeX = (short) (waypoint.getMinX() - 40);
            if (safeX < 0) safeX = (short) (waypoint.getMinX() + 50);
        }

        pos.x = safeX;
        pos.y = safeY;

        con.sendPacket(new SmResetPoint(pos.x, pos.y));

        pos.wantsToChangeMap = false;
    }

}
