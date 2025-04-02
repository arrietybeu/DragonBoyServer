package nro.server.service.model.entity.ai;

import lombok.Getter;
import lombok.Setter;
import nro.server.service.core.map.AreaService;
import nro.server.service.core.social.ChatService;
import nro.server.service.model.entity.Entity;
import nro.server.system.LogServer;
import nro.utils.Util;

@Setter
@Getter
public abstract class AbstractAI extends Entity implements AI {

    private AIState currentState;

    public final boolean isInState(AIState state) {
        return currentState == state;
    }

    public synchronized boolean setStateIfNot(AIState newState) {
        if (this.currentState == newState) return false;
        this.currentState = newState;
        return true;
    }

    /// this method is used to move the entity towards the target player.
    public void goToPlayer(Entity entity, Entity target) {
        try {
            var yTarget = target.getY();
            int dir = entity.getX() - target.getX() < 0 ? 1 : -1;
            int move = Util.nextInt(50, 100);
            entity.setX((short) (entity.getX() + (dir == 1 ? move : -move)));
            entity.setY(yTarget);
            AreaService.getInstance().playerMove(entity);
        } catch (Exception exception) {
            LogServer.LogException("MoveEventHandler.goToPlayer: " + exception.getMessage(), exception);
        }
    }

    public void entityChat(Entity entity, String message) {
        try {
            ChatService.getInstance().chatMap(entity, message);
        } catch (Exception exception) {
            LogServer.LogException("MoveEventHandler.entityChat: " + exception.getMessage(), exception);
        }
    }
}
