package nro.service.model.model.bot.types;

import nro.service.model.model.bot.Bot;
import nro.service.model.model.player.Player;

public class Yajiro extends Bot {

    @Override
    public long handleAttack(Player player, long damage) {
        return 0;
    }

    @Override
    public void dispose() {
    }
}
