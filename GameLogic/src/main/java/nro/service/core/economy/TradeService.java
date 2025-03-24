package nro.service.core.economy;

import lombok.Getter;
import nro.service.model.entity.player.Player;

public class TradeService {

    @Getter
    private static final TradeService instance = new TradeService();


    public void sendOpenUITrade(Player player) {
    }

}
