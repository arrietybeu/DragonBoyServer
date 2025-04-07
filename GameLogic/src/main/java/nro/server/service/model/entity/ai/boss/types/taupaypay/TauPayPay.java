package nro.server.service.model.entity.ai.boss.types.taupaypay;

import nro.consts.ConstBoss;
import nro.server.service.model.entity.ai.boss.*;

@ABossHandler(ConstBoss.TAU_PAY_PAY)
public class TauPayPay extends Boss {

    public TauPayPay(int id, BossFashion bossFashion) {
        super(id, bossFashion);
        this.controller = new TauPayPayController();
    }

}
