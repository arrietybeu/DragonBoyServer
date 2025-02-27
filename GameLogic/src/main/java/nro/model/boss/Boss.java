package nro.model.boss;

import nro.consts.ConstTypeObject;
import nro.model.LiveObject;

public class Boss extends LiveObject {

    public Boss() {
        this.setTypeObject(ConstTypeObject.TYPE_BOSS);
    }

    @Override
    public void update() {

    }

    @Override
    public void dispose() {
    }
}
