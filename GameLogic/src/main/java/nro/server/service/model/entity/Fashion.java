package nro.server.service.model.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class Fashion {

    protected byte flagPk = 0;
    protected short head = -1;

    public abstract short getHead();

    public abstract short getBody();

    public abstract short getLeg();

    public abstract short getMount();

    public abstract short getFlagBag();

    public abstract short getAura();

    public abstract byte getEffSetItem();

    public abstract short getIdHat();
}
