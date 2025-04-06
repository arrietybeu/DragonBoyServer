package nro.server.service.model.entity.ai.boss;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstTypeObject;
import nro.server.realtime.system.boss.BossAISystem;
import nro.server.service.core.map.AreaService;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.ai.AIState;
import nro.server.service.model.entity.ai.AbstractAI;

@Setter
@Getter
public abstract class Boss extends AbstractAI {

    private String textChat;

    private boolean isAutoDespawn;

    protected long lastTimeIdle;
    protected long lastTimeAttack;
    protected long lastTimeMove;

    // thời gian không có người chơi thì biến mất (giây)
    protected int afkTimeout;
    public int tickAfkTimeout;

    protected byte spawnType;

    // mảng chứa các id map để boss có thể xuất hiện
    protected int[] mapsId;

    public Boss(int id, BossFashion bossFashion) {
        this.setTypeObject(ConstTypeObject.TYPE_BOSS);
        this.setCurrentState(AIState.IDLE);
        this.setId(id);
        this.fashion = bossFashion;
    }

    @Override
    public long handleAttack(Entity entity, int type, long damage) {

        System.out.println("Boss.handleAttack: " + this.getName() + " received damage: " + damage);
        if (this.points.isDead()) {
            this.points.setDie();
            return 0;
        }

        this.points.subCurrentHp(damage);

        if (this.points.isDead()) {
            this.points.setDie();
        }

        return damage;
    }

    @Override
    public void dispose() {
        this.tickAfkTimeout = 0;
        AreaService.getInstance().playerExitArea(this);
        BossAISystem.getInstance().unregister(this);
    }

    @Override
    public AIState getState() {
        return this.getCurrentState();
    }
}
