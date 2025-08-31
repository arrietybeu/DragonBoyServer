package nro.server.engine.entity.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import nro.commons.consts.ConstsCmd;
import nro.server.model.ecs.component.*;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.model.templates.world.Waypoint;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.server_packets.PacketHelper;
import nro.server.network.nro.server_packets.handler.SmMapInfo;
import nro.server.network.nro.server_packets.handler.SmResetPoint;
import nro.server.model.world.World;
import nro.server.model.world.WorldMapInstance;
import nro.server.services.AreaService;
import nro.server.services.NotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arriety
 */
public final class MapChangeSystem extends IteratingSystem {

    private static final Logger log = LoggerFactory.getLogger(MapChangeSystem.class);

    private ComponentMapper<PositionComponent> posMapper;
    private ComponentMapper<InfoComponent> infoMapper;
    private ComponentMapper<PlayerComponent> clients;

    public MapChangeSystem() {
        super(Aspect.all(PositionComponent.class, InfoComponent.class, PlayerComponent.class));
    }

    @Override
    protected void process(int entityId) {
        try {
            handler(entityId);
        } catch (Throwable t) {
            PositionComponent pos = posMapper.get(entityId);
            PlayerComponent pc = clients.get(entityId);
            if (pos != null) pos.wantsToChangeMap = false;
            if (pc != null && pc.connection != null) {
                NotifyService.SendNotifyPlayer(pc.connection, "Có lỗi khi đổi map, đã đưa bạn về khu an toàn.");
                if (pos != null) {
                    keepInSafeZone(null, pc.connection, pos);
                }
            }
            String name = infoMapper.has(entityId) ? infoMapper.get(entityId).name : "unknown";
            Short mapId = (pos != null) ? pos.mapId : null;
            log.error("MapChangeSystem error entityId={} name={} mapId={}", entityId, name, mapId, t);
        }
    }

    private void handler(int entityId) {
        PositionComponent pos = posMapper.get(entityId);
        InfoComponent info = infoMapper.get(entityId);
        PlayerComponent player = clients.get(entityId);
        var client = player.connection;

        if (!pos.wantsToChangeMap) return;

        var currentMap = World.getInstance().getMap(pos.mapId);

        if (currentMap == null) {
            this.keepInSafeZone(null, client, pos);
            throw new RuntimeException("Current map is null for entity " + info + " with mapId " + pos.mapId);
        }
        var currentArea = currentMap.getInstance(pos.getAreaId());
        if (currentArea == null)
            throw new RuntimeException("Current area is null for entity " + info + " in map " + pos.mapId);

        var waypoint = currentMap.getWayPointInMap(pos.x, pos.y, info.id);

        if (waypoint == null) {
            keepInSafeZone(null, client, pos);
            NotifyService.SendNotifyPlayer(client, "Bạn không thể đi đến đây!");
            throw new RuntimeException("Waypoint is null for entity " + info + " with mapId " + pos.mapId);
        }

        var newArea = World.getInstance().getAvailableInstance(waypoint.getGoMap(), info.id);

        if (newArea == null) {
            this.keepInSafeZone(waypoint, client, pos);
            NotifyService.SendNotifyPlayer(client, "Khu vực này không có người quản lý, bạn không thể đi đến đây!");
            return;
        }

        if (!this.transferEntity(client, waypoint, pos, currentArea, newArea)) {
            return;
        }
        pos.wantsToChangeMap = false;
    }

    private void playerChangerMapByWayPoint() {

    }

    private void playerChangeArea() {

    }

    private void playerChangeMapByShip() {
    }


    private boolean transferEntity(NroConnection client, Waypoint waypoint, PositionComponent pos, WorldMapInstance currentArea, WorldMapInstance newArea) {
        if (newArea == null) {
            this.keepInSafeZone(waypoint, client, pos);
            NotifyService.SendNotifyPlayer(client, "Khu vực này không có người quản lý, bạn không thể đi đến đây!");
            return false;
        }

        // TODO check thêm nếu player chưa hoàn thiện nhiệm vụ thì không cho đi

        if (newArea.isFullPlayer()) {
            this.keepInSafeZone(waypoint, client, pos);
            NotifyService.SendNotifyPlayer(client, "Khu vực này đã đầy người chơi, bạn không thể đi đến đây!");
            return false;
        }

        int xNew = waypoint.getGoX();
        int yNew = waypoint.getGoY();


        this.entityEnterArea(client, pos,currentArea, newArea, xNew, yNew);

        this.sendMessageChangerMap(client, pos);

        return true;
    }


    private void entityEnterArea(NroConnection client, PositionComponent pos, WorldMapInstance oldArea, WorldMapInstance newArea, int xNew, int yNew) {
        // xoa entity khỏi area cũ
        this.playerExitArea(client, pos, oldArea);

        // add entity vào area mới
        newArea.addEntity(client.getEntity().getId());

        pos.mapId = (short) newArea.getParent().getId();
        pos.setAreaId(newArea.getInstanceId());
        pos.x = (short) xNew;
        pos.y = (short) yNew;

        AreaService.getInstance().sendMyInfoToPlayersInZone(client);
    }


    public void playerExitArea(NroConnection client, PositionComponent pos, WorldMapInstance oldArea) {
        if (pos == null || oldArea == null) return;
        if (!oldArea.removeEntity(client.getEntity().getId())) {
            throw new RuntimeException("Can't remove entity " + client.getPlayerID() + " from old area");
        }

        AreaService.getInstance().sendPacketPlayerExitArea(client, oldArea);
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

    private void sendMessageChangerMap(NroConnection client, PositionComponent pos) {
        client.sendPacket(PacketHelper.empty(ConstsCmd.MAP_CLEAR));
        // TODO send statmina, send current hp mp
        client.sendPacket(new SmMapInfo(pos));
    }

}
