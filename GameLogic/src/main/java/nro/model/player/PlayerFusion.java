package nro.model.player;

import lombok.Data;

@Data
public class PlayerFusion {

    private final Player player;
    private byte typeFusion;
    private long lastTimeFusion;
}
