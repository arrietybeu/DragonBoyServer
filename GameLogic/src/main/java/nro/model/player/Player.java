package nro.model.player;

import lombok.Getter;
import nro.model.LiveObject;

public class Player extends LiveObject {

    @Getter
    private final Currencies currencies;

    public Player() {
        this.currencies = new Currencies(this);
    }

}
