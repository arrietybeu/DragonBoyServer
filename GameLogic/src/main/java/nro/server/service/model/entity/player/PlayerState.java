package nro.server.service.model.entity.player;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstMenu;
import nro.server.service.core.economy.TradeService;

@Getter
@Setter
public class PlayerState {

    private final Player player;

    private byte typeTransport;

    private int idTrade;
    private int indexMenu;
    private int idItemTask;

    private long lastTimeLive;
    private long lastTimeChangeFlag;
    private long lastTimeChangeMap;
    private long lastTimePickItem;
    private long lastTimeAddExp;
    private long lastTimeTransport;
    private long lastTimeChangeArea;

    private boolean isInVisible;
    private boolean isLockMove;

    public PlayerState(Player player) {
        this.player = player;
    }

    public boolean isBaseMenu() {
        return this.indexMenu == ConstMenu.BASE_MENU;
    }

    public void dispose() {
        TradeService.getInstance().cancelTrade(player);
    }

}
