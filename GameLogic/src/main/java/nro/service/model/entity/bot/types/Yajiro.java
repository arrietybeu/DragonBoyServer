package nro.service.model.bot.types;

import nro.service.model.bot.Bot;
import nro.service.model.player.Player;

public class Yajiro extends Bot {

    @Override
    public long handleAttack(Player player, int type, long damage) {
        return 0;
    }

    @Override
    public void dispose() {
    }
}
