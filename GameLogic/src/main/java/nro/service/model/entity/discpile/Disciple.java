package nro.service.model.entity.discpile;

import nro.service.model.entity.BaseModel;
import nro.service.model.entity.player.Player;

public class Disciple extends BaseModel {

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
