package nro.model.bot;

import lombok.Getter;
import lombok.Setter;
import nro.model.LiveObject;
import nro.model.player.Player;

@Getter
@Setter
public abstract class Bot extends LiveObject {

    @Override
    public void update() {
    }

}
