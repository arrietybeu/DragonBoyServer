package nro.model.player;

import lombok.Data;

@Data
public class PlayerFashion {

    private final Player player;

    private short head = -1;
    private short body = -1;
    private short leg = -1;

    private short flagBag = -1;

    public PlayerFashion(Player player) {
        this.player = player;
    }
}
