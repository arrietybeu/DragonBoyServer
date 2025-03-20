package nro.service.model.model.player;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerFusion {

    private final Player player;
    private byte typeFusion;
    private long lastTimeFusion;

    public PlayerFusion(Player pLayer) {
        this.player = pLayer;
    }

    public Player getPlayer() {
        return player;
    }

    public byte getTypeFusion() {
        return typeFusion;
    }

    public long getLastTimeFusion() {
        return lastTimeFusion;
    }
}
