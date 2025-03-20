package nro.service.model.model.boss;

import nro.consts.ConstTypeObject;
import nro.service.model.model.LiveObject;
import nro.service.model.model.player.Player;

public class Boss extends LiveObject {

    public Boss() {
        this.setTypeObject(ConstTypeObject.TYPE_BOSS);
    }

    @Override
    public void update() {
    }

    @Override
    public long handleAttack(Player player, long damage) {
        return 0;
    }

    @Override
    public void dispose() {
    }
}
