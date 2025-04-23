package nro.server.service.model.entity.bot.types;

import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.bot.Bot;
import nro.server.service.model.entity.player.Player;

public class Yajiro extends Bot {

    @Override
    public synchronized long handleAttack(Entity entityAttack, int type, long damage) {
        return 0;
    }

    @Override
    protected void onDie(Entity killer) {

    }

    @Override
    public void dispose() {
    }
}
