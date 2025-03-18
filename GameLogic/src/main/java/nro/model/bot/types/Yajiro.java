package nro.model.bot.types;

import nro.model.bot.Bot;
import nro.model.player.Player;

public class Yajiro extends Bot {

    @Override
    public long handleAttack(Player player, long damage) {
        return 0;
    }

    @Override
    public void dispose() {
    }
}
