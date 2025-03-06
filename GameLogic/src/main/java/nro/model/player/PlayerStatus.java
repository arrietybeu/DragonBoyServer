package nro.model.player;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PlayerStatus {

    private final Player player;

    private int indexMenu;
    private boolean isLockMove;
    private long lastTimeLive;
    private long lastTimeChangeFlag;
    private long lastTimeChangeMap;
    private long lastTimePickItem;

    private int teleport = 0;


    public PlayerStatus(Player player) {
        this.player = player;
    }
}
