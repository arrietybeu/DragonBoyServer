package nro.service.model.player;

import lombok.Getter;

@Getter
public class PlayerAdministrator {

    private final Player player;

    public PlayerAdministrator(Player player) {
        this.player = player;
    }
}
