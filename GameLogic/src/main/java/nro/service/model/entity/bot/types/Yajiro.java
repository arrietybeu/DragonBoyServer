package nro.service.model.entity.bot.types;

import nro.service.model.entity.bot.Bot;
import nro.service.model.entity.player.Player;

public class Yajiro extends Bot {

    @Override
    public long handleAttack(Player player, int type, long damage) {
        return 0;
    }

    @Override
    public void dispose() {
    }
}
