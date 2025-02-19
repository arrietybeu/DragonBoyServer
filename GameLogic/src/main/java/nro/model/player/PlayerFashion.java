package nro.model.player;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlayerFashion {

    private final Player player;

    private byte flag = 0;

    private short head = -1;
    private short body = -1;
    private short leg = -1;
    private short flagBag = -1;

    public PlayerFashion(Player player) {
        this.player = player;
    }

    @Override
    public String toString() {
        return "PlayerFashion{" +
                "player=" + player +
                ", flag=" + flag +
                ", head=" + head +
                ", body=" + body +
                ", leg=" + leg +
                ", flagBag=" + flagBag +
                '}';
    }
}
