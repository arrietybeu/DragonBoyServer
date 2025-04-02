package nro.server.service.model.entity.ai.boss.types;

import nro.consts.ConstBoss;
import nro.server.service.model.entity.ai.AIState;
import nro.server.service.model.entity.ai.boss.*;
import nro.server.service.model.entity.player.Player;

@ABossHandler(ConstBoss.TAU_PAY_PAY)
public class TauPayPay extends Boss {

    public TauPayPay(int id, BossPoints bossPoint, BossFashion bossFashion, BossSkill bossSkill) {
        super(id, bossPoint, bossFashion, bossSkill);
    }

    @Override
    public long handleAttack(Player player, int type, long damage) {
        return super.handleAttack(player, type, damage);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public AIState getState() {
        return super.getState();
    }

}
