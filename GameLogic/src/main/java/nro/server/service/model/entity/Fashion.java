package nro.server.service.model.entity;

import lombok.Setter;

@Setter
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

    public abstract byte getFlagPk();

    public abstract short getHead();

    public abstract short getBody();

    public abstract short getLeg();

    public abstract short getMount();

    public abstract short getFlagBag();

    public abstract short getAura();

    public abstract byte getEffSetItem();

    public abstract short getIdHat();

}
