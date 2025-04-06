package nro.server.service.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public abstract class Fashion {

    protected byte flagPk = 0;
    protected byte effSetItem = -1;

    protected short headDefault = -1;

    protected short head, body, leg, mount, flagBag, aura, idHat = -1;

    public abstract void updateFashion();

    public abstract Fashion copy();

}
