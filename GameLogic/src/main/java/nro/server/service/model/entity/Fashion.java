package nro.server.service.model.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class Fashion {

    protected byte flagPk = 0;
    protected short head = -1;
    protected short body = -1;
    protected short leg = -1;
    protected short mount = -1;
    protected short flagBag = -1;
    protected short aura = -1;
    protected byte effSetItem = -1;
    protected short idHat = -1;

}
