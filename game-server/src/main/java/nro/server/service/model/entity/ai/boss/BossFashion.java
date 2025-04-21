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
    public BossFashion copy() {
        return new BossFashion(head, body, leg, mount, flagBag, aura, effSetItem, idHat);
    }

    @Override
    public void updateFashion() {
    }
}
