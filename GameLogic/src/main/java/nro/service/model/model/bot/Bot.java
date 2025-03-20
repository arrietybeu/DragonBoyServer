package nro.service.model.model.bot;

import lombok.Getter;
import lombok.Setter;
import nro.service.model.model.LiveObject;

@Getter
@Setter
public abstract class Bot extends LiveObject {

    @Override
    public void update() {
    }

}
