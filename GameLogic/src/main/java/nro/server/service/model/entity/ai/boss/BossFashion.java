package nro.server.service.model.entity.ai.boss;

import nro.server.service.model.entity.Fashion;

public class BossFashion extends Fashion {

    public BossFashion(short head, short body, short leg, short mount, short flagBag, short aura, byte effSetItem, short idHat) {
        this.setHead(head);
        this.setBody(body);
        this.setLeg(leg);
        this.setMount(mount);
        this.setFlagBag(flagBag);
        this.setAura(aura);
        this.setEffSetItem(effSetItem);
        this.setIdHat(idHat);
    }

    @Override
    public byte getFlagPk() {
        return super.flagPk;
    }

    @Override
    public short getHead() {
        return super.head;
    }

    @Override
    public short getBody() {
        return super.body;
    }

    @Override
    public short getLeg() {
        return super.leg;
    }

    @Override
    public short getMount() {
        return super.mount;
    }

    @Override
    public short getFlagBag() {
        return super.flagBag;
    }

    @Override
    public short getAura() {
        return super.aura;
    }

    @Override
    public byte getEffSetItem() {
        return super.effSetItem;
    }

    @Override
    public short getIdHat() {
        return super.idHat;
    }
}
