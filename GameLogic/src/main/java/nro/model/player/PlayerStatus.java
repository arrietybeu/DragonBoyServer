package nro.model.player;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerStatus {

    private final Player player;

    private int indexMenu;
    private boolean isLockMove;
    private long lastTimeLive;

    public PlayerStatus(Player player) {
        this.player = player;
    }
}












