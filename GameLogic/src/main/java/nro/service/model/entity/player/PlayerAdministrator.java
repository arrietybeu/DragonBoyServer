package nro.service.model.entity.player;

import lombok.Getter;

@Getter
public class PlayerAdministrator {

    private final Player player;

    public PlayerAdministrator(Player player) {
        this.player = player;
    }
}
