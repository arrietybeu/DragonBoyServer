package nro.server.service.core.economy;

import lombok.Getter;
import nro.server.service.model.entity.player.Player;

public class TradeService {

    @Getter
    private static final TradeService instance = new TradeService();


    public void sendOpenUITrade(Player player) {
    }

}
