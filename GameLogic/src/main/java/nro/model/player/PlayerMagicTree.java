package nro.model.player;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerMagicTree {

    private final Player player;

    private byte level;
    private int currPeas;

    public PlayerMagicTree(Player player) {
        this.player = player;
    }

    public byte getMaxPea() {
        if (this.level == 10) {
            return 2;
        }
        return (byte) ((this.level - 1) * 2 + 5);
    }
}
