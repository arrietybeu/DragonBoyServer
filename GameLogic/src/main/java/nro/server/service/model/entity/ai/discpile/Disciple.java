package nro.server.service.model.entity.ai.discpile;

import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.player.Player;

public class Disciple extends Entity {

    @Override
    public long handleAttack(Player player, int type, long damage) {
        return 0;
    }

    @Override
    public void dispose() {
    }
}
