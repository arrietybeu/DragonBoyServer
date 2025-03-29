package nro.server.service.model.entity.player;

import nro.server.service.model.entity.Fusion;

public class PlayerFusion extends Fusion {

    private final Player player;

    public PlayerFusion(Player player) {
        this.player = player;
    }

}
