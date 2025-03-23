package nro.service.model.boss;

import nro.consts.ConstTypeObject;
import nro.service.model.LiveObject;
import nro.service.model.player.Player;

public class Boss extends LiveObject {

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
