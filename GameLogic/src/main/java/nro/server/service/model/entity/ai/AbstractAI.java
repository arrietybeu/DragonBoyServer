package nro.server.service.model.entity.ai;

import lombok.Getter;
import lombok.Setter;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;

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
        LogServer.LogInfo("AI State changed: " + this.getName() + " -> " + newState);
    }

    public Player getEntityTargetAsPlayer() {
        if (this.entityTarget instanceof Player player) {

            // kiểm tra xem player có trong map không
            if (player.getArea() != this.getArea()) {
                return null;
            }
            if (this.getArea().getPlayer(player.getId()) == null) {
                return null;
            }
            return player;
        }
        return null;
    }

    public boolean isValidTarget(Entity target) {
        if (target == null) return true;
        if (target.getPoints().isDead()) return true;
        return target.getArea() != this.getArea();
    }
}
