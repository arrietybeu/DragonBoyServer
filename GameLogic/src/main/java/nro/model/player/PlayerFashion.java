package nro.model.player;

import lombok.Data;

@Data
public class PlayerFashion {

    private final Player player;

    private int head;

    public PlayerFashion(Player player) {
        this.player = player;
    }
}
