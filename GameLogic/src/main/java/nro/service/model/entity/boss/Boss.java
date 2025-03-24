package nro.service.model.entity.boss;

import nro.consts.ConstTypeObject;
import nro.service.model.entity.BaseModel;
import nro.service.model.entity.player.Player;

public class Boss extends BaseModel {

    public Boss() {
        this.setTypeObject(ConstTypeObject.TYPE_BOSS);
    }

    @Override
    public void update() {
    }

    @Override
    public long handleAttack(Player player, int type, long damage) {
        return 0;
    }

    @Override
    public void dispose() {
    }
}
