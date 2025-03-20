package nro.service.model.discpile;

import nro.service.model.LiveObject;
import nro.service.model.player.Player;

public class Disciple extends LiveObject {

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
