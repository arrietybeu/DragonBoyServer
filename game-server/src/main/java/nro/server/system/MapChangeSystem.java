package nro.server.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import nro.server.model.ecs.component.*;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.model.ecs.component.player.TaskComponent;
import nro.server.model.templates.world.Waypoint;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.server_packets.handler.SmChatTheGioi;
import nro.server.network.nro.server_packets.handler.SmResetPoint;
import nro.server.world.World;
import nro.server.world.WorldMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arriety
 */
public class MapChangeSystem extends IteratingSystem {

    private static final Logger log = LoggerFactory.getLogger(MapChangeSystem.class);

    private ComponentMapper<PositionComponent> posMapper;
    private ComponentMapper<TaskComponent> taskMapper;
    private ComponentMapper<InfoComponent> infoMapper;
    private ComponentMapper<PlayerComponent> clients;

    public MapChangeSystem() {
        super(Aspect.all(PositionComponent.class));
    }

    @Override
    protected void process(int entityId) {
        PositionComponent pos = posMapper.get(entityId);
        InfoComponent info = infoMapper.get(entityId);
        PlayerComponent player = clients.get(entityId);
        var client = player.connection;

        if (!pos.wantsToChangeMap) return;

        log.info("Entity {} requesting map change", entityId);

        var currentMap = World.getInstance().getMap(pos.mapId);

        if (currentMap == null)
            throw new RuntimeException("Current map is null for entity " + info + " with mapId " + pos.mapId);
        var currentArea = currentMap.getInstance(pos.areaId);
        if (currentArea == null)
            throw new RuntimeException("Current area is null for entity " + info + " in map " + pos.mapId);

        var waypoint = currentMap.getWayPointInMap(pos.x, pos.y, info.id);

        if (waypoint == null) {
            keepInSafeZone(entityId, null, client);
            return;
        }

        var newArea = World.getInstance().getAvailableInstance(waypoint.getGoMap(), info.id);

        if (newArea == null) {
            this.keepInSafeZone(entityId, waypoint, client);
            client.sendPacket(new SmChatTheGioi("Map không tồn tại"));
            return;
        }

        

        pos.wantsToChangeMap = false;
    }

    private void keepInSafeZone(int entityId, Waypoint waypoint, NroConnection con) {
        PositionComponent pos = posMapper.get(entityId);
        if (pos == null) return;

        short safeX = pos.x;
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
    }

}
