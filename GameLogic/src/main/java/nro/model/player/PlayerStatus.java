package nro.model.player;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerStatus {

    private final Player player;

    private int indexMenu;

    public PlayerStatus(Player player) {
        this.player = player;
    }
}












