package nro.server.services.player;

import lombok.NoArgsConstructor;
import nro.server.dao.PlayerDAO;
import nro.server.engine.GameWorld;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.network.nro.NroConnection;
import nro.server.utils.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arriety
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class PlayerLeaveWorldService {

    private static final Logger log = LoggerFactory.getLogger(PlayerLeaveWorldService.class);

    public static void leaveWorld(NroConnection con) {
        try {

            var entity = con.getEntity();
            var connection = entity.getComponent(PlayerComponent.class).connection;
            var positionComponent = entity.getComponent(PositionComponent.class);

            var zone = MapUtils.findZone(positionComponent.mapId, positionComponent.getAreaId());
            if (zone == null) {
                log.warn("No map map zone found for map ID {} and area ID {}. Cannot leave world.",
                        positionComponent.mapId, positionComponent.getAreaId());
                return;
            }

            MapUtils.detachFromZone(entity, zone);

            PlayerDAO.savePlayerEntity(entity);

            // Delete entity from the world ecs
            GameWorld.getInstance().deleteEntity(entity.getId());

            // Detach player entity
            connection.detachPlayerEntity();

            log.info("Player with account {} has left the world.", connection.getAccount());
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

}
