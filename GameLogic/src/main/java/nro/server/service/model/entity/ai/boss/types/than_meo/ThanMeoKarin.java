package nro.server.service.model.entity.ai.boss.types.than_meo;

import nro.consts.ConstBoss;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.ai.boss.ABossHandler;
import nro.server.service.model.entity.ai.boss.Boss;
import nro.server.service.model.entity.ai.boss.BossFashion;

@ABossHandler(ConstBoss.THAN_MEO_KARIN)
public class ThanMeoKarin extends Boss {

    public ThanMeoKarin(int id, BossFashion bossFashion) {
        super(id, bossFashion);
        this.controller = new ThanMeoKarinController();
    }

    @Override
    public void onDie(Entity entity) {
    }
}
