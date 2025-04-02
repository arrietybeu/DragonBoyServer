package nro.server.service.model.entity.ai;

import lombok.Getter;
import lombok.Setter;
import nro.server.service.model.entity.Entity;

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
}
