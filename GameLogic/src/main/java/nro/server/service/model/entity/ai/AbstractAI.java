package nro.server.service.model.entity.ai;

import lombok.Getter;
import lombok.Setter;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.player.Player;

@Setter
@Getter
public abstract class AbstractAI extends Entity implements AI {

    private AIState currentState;
    private Entity entityTarget;

    // thời gian hồi sinh sau khi chết
    protected int respawnTime;

    public final boolean isInState(AIState state) {
        return currentState == state;
    }

    public synchronized void setState(AIState newState) {
        if (this.currentState == newState) return;
        this.currentState = newState;
    }

    public Player getEntityTargetAsPlayer() {
        if (this.entityTarget instanceof Player player) {
            return player;
        }
        return null;
    }


}
