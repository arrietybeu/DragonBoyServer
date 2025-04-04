package nro.server.service.model.entity.ai.handler;

import nro.server.service.core.map.AreaService;
import nro.server.service.model.entity.Entity;
import nro.server.system.LogServer;
import nro.utils.Util;

public class MoveEventHandler {

    /// this method is used to move the entity towards the target player.
    public static void goToPlayer(Entity entity, Entity target) {
        try {
            var yTarget = target.getY();
            int direction = entity.getX() - target.getX() < 0 ? 1 : -1;
            int move = Util.nextInt(50, 100);
            entity.setX((short) (entity.getX() + (direction == 1 ? move : -move)));
            entity.setY(yTarget);
            AreaService.getInstance().playerMove(entity);
        } catch (Exception exception) {
            LogServer.LogException("MoveEventHandler.goToPlayer: " + exception.getMessage(), exception);
        }
    }

}
