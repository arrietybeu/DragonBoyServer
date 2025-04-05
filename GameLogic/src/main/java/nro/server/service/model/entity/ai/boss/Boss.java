package nro.server.service.model.entity.ai.boss;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstTypeObject;
import nro.server.realtime.system.boss.BossAISystem;
import nro.server.service.model.entity.ai.AIState;
import nro.server.service.model.entity.ai.AbstractAI;
import nro.server.service.model.entity.player.Player;

@Setter
@Getter
public abstract class Boss extends AbstractAI {

    protected long lastTimeIdle;
    protected long lastTimeAttack;
    protected long lastTimeMove;

    // mảng chứa các id map để boss có thể xuất hiện
    protected int[] mapsId;

    // thời gian không có người chơi thì biến mất (giây)
    protected int afkTimeout;

    private byte spawnType;

    public Boss(int id, BossPoints bossPoint, BossFashion bossFashion, BossSkill bossSkill) {
        this.setTypeObject(ConstTypeObject.TYPE_BOSS);
        this.setCurrentState(AIState.IDLE);
        this.setId(id);
        this.points = bossPoint;
        this.fashion = bossFashion;
        this.skills = bossSkill;
    }

    @Override
    public long handleAttack(Player player, int type, long damage) {

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
        BossAISystem.getInstance().unregister(this);
    }

    @Override
    public AIState getState() {
        return this.getCurrentState();
    }
}
