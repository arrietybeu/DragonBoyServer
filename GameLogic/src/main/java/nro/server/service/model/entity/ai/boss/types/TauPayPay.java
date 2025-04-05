package nro.server.service.model.entity.ai.boss.types;

import nro.consts.ConstBoss;
import nro.server.service.model.entity.ai.boss.*;

@ABossHandler(ConstBoss.TAU_PAY_PAY)
public class TauPayPay extends Boss {

    public TauPayPay(int id, BossPoints bossPoint, BossFashion bossFashion, BossSkill bossSkill) {
        super(id, bossPoint, bossFashion, bossSkill);
    }

}
