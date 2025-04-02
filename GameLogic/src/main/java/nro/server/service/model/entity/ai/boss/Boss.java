package nro.server.service.model.entity.ai.boss;

import nro.consts.ConstTypeObject;
import nro.server.service.model.entity.ai.AIState;
import nro.server.service.model.entity.ai.AbstractAI;
import nro.server.service.model.entity.player.Player;

public abstract class Boss extends AbstractAI {

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
        return 0;
    }

    @Override
    public void dispose() {
    }

    @Override
    public AIState getState() {
        return this.getCurrentState();
    }
}
