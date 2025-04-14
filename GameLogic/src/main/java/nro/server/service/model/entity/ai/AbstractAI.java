package nro.server.service.model.entity.ai;

import lombok.Getter;
import lombok.Setter;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;

@Setter
@Getter
public abstract class AbstractAI extends Entity implements AI {

    protected AIState currentState;
    protected Entity entityTarget;

    // thời gian hồi sinh sau khi chết
    protected int respawnTime;
    public int tickAfkTimeout;

    protected long stateStartTime;        // thời điểm bắt đầu vào state
    protected long requiredStateDelay = 0; // thời gian phải chờ trước khi được phép đổi state
    protected AIState nextState = null;    // state tiếp theo khi hết delay

    protected boolean isLockMove;

    public final boolean isInState(AIState state) {
        return currentState == state;
    }

    public synchronized void setState(AIState newState) {
        if (this.currentState == newState) return;
        this.currentState = newState;
        this.stateStartTime = System.currentTimeMillis();
        this.requiredStateDelay = 0;
        this.tickAfkTimeout = 0;
        this.nextState = null;
        LogServer.LogInfo("AI State changed: " + this.getName() + " -> " + newState);
    }

    /**
     * @param state (trạng thái hiện tại)
     * @param delayMillis (thời gian delay trước khi chuyển sang trạng thái tiếp theo)
     * @param nextState (trạng thái tiếp theo)
     * <p>
     * <pre>
     *  * Ví Dụ:
     *   <pre>
     *    {@code
     *     if (boss.getNextState() != AIState.GO_TO_MAP) {
     *         boss.onEnterStateWithDelay(AIState.IDLE, 3000, AIState.GO_TO_MAP);
     *     }
     *     boss.trySwitchToNextState();
     *    }
     *   </pre>
     * </pre>
     * </p>
     */
    public synchronized void onEnterStateWithDelay(AIState state, long delayMillis, AIState nextState) {
        this.currentState = state;
        this.stateStartTime = System.currentTimeMillis();
        this.requiredStateDelay = delayMillis;
        this.tickAfkTimeout = 0;
        this.nextState = nextState;
        LogServer.LogInfo("onEnterStateWithDelay: " + this.getName() + " -> " + state + " with delay: " + delayMillis);
    }

    public synchronized void onEnterState(AIState state) {
        this.currentState = state;
        this.stateStartTime = System.currentTimeMillis();
        this.requiredStateDelay = 0;
        this.tickAfkTimeout = 0;
        this.nextState = null;
    }

    // đặt thời gian delay cho state tiếp theo
    public boolean shouldSwitchToNextState() {
        return nextState != null && System.currentTimeMillis() - stateStartTime >= requiredStateDelay;
    }

    public void trySwitchToNextState() {
        if (shouldSwitchToNextState()) {
            onEnterState(nextState);
        }
    }

    public Player getEntityTargetAsPlayer() {
        if (this.entityTarget instanceof Player player
                && player.getArea() == this.getArea()
                && this.getArea().getPlayer(player.getId()) != null) {
            return player;
        }
        return null;
    }

    public boolean isValidTarget(Entity target) {
        return target == null || target.getPoints().isDead() || target.getArea() != this.getArea();
    }

}
