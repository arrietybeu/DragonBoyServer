package nro.model.player;

import lombok.Data;

@Data
public class PlayerFashion {

    private final Player player;

    private short head;
    private short body;
    private short leg;

    private short flagBag;

    public PlayerFashion(Player player) {
        this.player = player;
    }
}
