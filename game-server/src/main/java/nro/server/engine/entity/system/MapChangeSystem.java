package nro.server.engine.entity.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import nro.commons.consts.ConstsCmd;
import nro.server.model.ecs.component.*;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.model.ecs.component.player.QuestInstanceComponent;
import nro.server.model.templates.world.Waypoint;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.server_packets.PacketHelper;
import nro.server.network.nro.server_packets.handler.SmChatTheGioi;
import nro.server.network.nro.server_packets.handler.SmMapInfo;
import nro.server.network.nro.server_packets.handler.SmResetPoint;
import nro.server.model.world.World;
import nro.server.model.world.WorldMapInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arriety
 */
public class MapChangeSystem extends IteratingSystem {

    private static final Logger log = LoggerFactory.getLogger(MapChangeSystem.class);

    private ComponentMapper<PositionComponent> posMapper;
    private ComponentMapper<QuestInstanceComponent> taskMapper;
    private ComponentMapper<InfoComponent> infoMapper;
    private ComponentMapper<PlayerComponent> clients;

    public MapChangeSystem() {
        super(Aspect.all(PositionComponent.class, InfoComponent.class, PlayerComponent.class));
    }

    @Override
    protected void process(int entityId) {
        PositionComponent pos = posMapper.get(entityId);
        InfoComponent info = infoMapper.get(entityId);
        PlayerComponent player = clients.get(entityId);
        var client = player.connection;

        if (!pos.wantsToChangeMap) return;

        log.info("Entity {} requesting map change x {} y {} map id {}", entityId + " (" + info.name + ")", pos.x, pos.y, pos.mapId);

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
            client.sendPacket(new SmChatTheGioi("Bạn không thể đi đến đây!"));
            throw new RuntimeException("Waypoint is null for entity " + info + " with mapId " + pos.mapId);
        }

        var newArea = World.getInstance().getAvailableInstance(waypoint.getGoMap(), info.id);

        if (newArea == null) {
            this.keepInSafeZone(waypoint, client, pos);
            client.sendPacket(new SmChatTheGioi("Khu vực này không có người quản lý, bạn không thể đi đến đây!"));
            return;
        }

        log.info("player wants to change map: {} to mapId: {} at areaId: {}", info.name, waypoint.getGoMap(), newArea.getInstanceId());

        if (!this.transferEntity(client, waypoint, pos, currentArea, newArea)) {
            return;
        }
        pos.wantsToChangeMap = false;
    }

    private boolean transferEntity(NroConnection client, Waypoint waypoint, PositionComponent pos, WorldMapInstance currentArea, WorldMapInstance newArea) {
        if (newArea == null) {
            this.keepInSafeZone(waypoint, client, pos);

            client.sendPacket(new SmChatTheGioi("Khu vực này không có người quản lý, bạn không thể đi đến đây!"));
            return false;
        }

        if (newArea.isFullPlayer()) {
            this.keepInSafeZone(waypoint, client, pos);
            client.sendPacket(new SmChatTheGioi("Khu vực này đã đầy người chơi, bạn không thể đi đến đây!"));
            return false;
        }

        // xoa entity ra khoi khu  cu
        this.playerExitArea(client, pos, currentArea);

        int xNew = waypoint.getGoX();
        int yNew = waypoint.getGoY();

        // add entity vào area mới
        newArea.addEntity(client.getEntity().getId());

        pos.mapId = (short) waypoint.getGoMap();
        pos.setAreaId(newArea.getInstanceId());
        pos.x = (short) xNew;
        pos.y = (short) yNew;

        this.sendMessageChangerMap(client, pos);
//        AreaService.getInstance().sendInfoAllLiveObjectsTo(client.getEntity());
        return true;
    }

    public void sendMessageChangerMap(NroConnection client, PositionComponent pos) {
        client.sendPacket(PacketHelper.empty(ConstsCmd.MAP_CLEAR));
        client.sendPacket(new SmMapInfo(pos));
    }


    public void playerExitArea(NroConnection client, PositionComponent pos, WorldMapInstance oldArea) {
        if (pos == null || oldArea == null) return;
        if (!oldArea.removeEntity(client.getPlayerID())) {
            throw new RuntimeException("Can't remove entity " + client.getPlayerID() + " from old area");
        }
        log.info("Player with account {} has left the area {} in map {} area SIZE {}.", client.getAccount(), oldArea.getInstanceId(), pos.mapId, oldArea.getPlayerCount());
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
