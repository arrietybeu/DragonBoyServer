package nro.server.service.model.entity.player;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nro.consts.ConstMenu;

@Getter
@Setter
@ToString
public class PlayerStatus {

    private final Player player;

    private boolean isInVisible;

    private int indexMenu;
    private boolean isLockMove;
    private long lastTimeLive;
    private long lastTimeChangeFlag;
    private long lastTimeChangeMap;
    private long lastTimePickItem;
    private long lastTimeAddExp;
    private long lastTimeTransport;
    private long lastTimeChangeArea;

    private int idItemTask;
    private byte typeTransport;

    public PlayerStatus(Player player) {
        this.player = player;
    }

    public boolean isBaseMenu() {
        return this.indexMenu == ConstMenu.BASE_MENU;
    }

}
