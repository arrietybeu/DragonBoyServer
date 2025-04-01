package nro.server.service.model.entity.boss;

import nro.consts.ConstTypeObject;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.player.Player;

public class Boss extends Entity {

    public Boss() {
        this.setTypeObject(ConstTypeObject.TYPE_BOSS);
    }

    @Override
    public long handleAttack(Player player, int type, long damage) {
        return 0;
    }

    @Override
    public void dispose() {
    }
}
