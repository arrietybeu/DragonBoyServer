package nro.server.services.player;

import nro.server.engine.entity.GameWorld;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.network.nro.NroConnection;
import nro.server.model.world.World;
import nro.server.model.world.WorldMapInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arriety
 */
public class PlayerLeaveWorldService {

    private static final Logger log = LoggerFactory.getLogger(PlayerLeaveWorldService.class);

    public static void leaveWorld(NroConnection con) {

        var entity = con.getEntity();
        var connection = entity.getComponent(PlayerComponent.class).connection;

        var positionComponent = entity.getComponent(PositionComponent.class);

        var world = World.getInstance().getMap(positionComponent.mapId);
        WorldMapInstance worldMapInstance = world.getWorldMapInstance(positionComponent.getAreaId());

        if (worldMapInstance == null) {
            // TODO fix me client bị kick vif treo lâu thì lỗi này sẽ xảy ra do bị kick khỏi map 2 lâần chắc ko sao dau hehe

            log.warn("No world map instance found for map ID {} and area ID {}. Cannot leave world.", positionComponent.mapId, positionComponent.getAreaId());
            return;
        }

        if (!worldMapInstance.removeEntity(entity.getId())) {
            log.error("Failed to remove player entity with ID {} from world map instance (map ID: {}, area ID: {}).", entity.getId(), positionComponent.mapId, positionComponent.getAreaId());
        }

        // Delete entity from the world ecs
        GameWorld.getInstance().deleteEntity(entity.getId());

        // Detach player entity
        connection.detachPlayerEntity();

        log.info("Player with account {} has left the world.", connection.getAccount());
    }

}
