package nro.server.services.player;

import com.artemis.Entity;
import nro.server.engine.GameWorld;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.world.World;
import nro.server.world.WorldMapInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arriety
 */
public class PlayerLeaveWorldService {

    private static final Logger log = LoggerFactory.getLogger(PlayerLeaveWorldService.class);

    public static void leaveWorld(Entity entity) {

        var connection = entity.getComponent(PlayerComponent.class).connection;

        var positionComponent = entity.getComponent(PositionComponent.class);

        var world = World.getInstance().getMap(positionComponent.mapId);
        WorldMapInstance worldMapInstance = world.getWorldMapInstance(positionComponent.areaId);

        if (worldMapInstance == null) {
            log.warn("No world map instance found for map ID {} and area ID {}. Cannot leave world.",
                    positionComponent.mapId, positionComponent.areaId);
            return;
        }

        worldMapInstance.removeEntity(entity.getId());

        // Delete entity from the world ecs
        GameWorld.getInstance().deleteEntity(entity.getId());

        // Detach player entity
        connection.detachPlayerEntity();

        log.info("Player with account {} has left the world.", connection.getAccount());
    }

}
