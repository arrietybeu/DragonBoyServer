package nro.server.service.model.entity.ai.boss.types.taupaypay;

import nro.consts.ConstBoss;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.ai.boss.*;
import nro.server.service.model.entity.player.Player;

@ABossHandler(ConstBoss.TAU_PAY_PAY)
public class TauPayPay extends Boss {

    public TauPayPay(int id, BossFashion bossFashion) {
        super(id, bossFashion);
        this.controller = new TauPayPayController();
    }

    @Override
    public void onDie(Entity entity) {
        if (entity instanceof Player player) {
            player.getPlayerTask().checkDoneTaskKillBoss(this.getId());
        }
    }

}
