package nro.model.pet;

import nro.consts.ConstTypeObject;
import nro.model.player.Player;

public class MiniPet extends Player {

    private Player subject;

    public MiniPet(Player subject) {
        this.subject = subject;
        this.setTypeObject(ConstTypeObject.TYPE_MINI_PET);
        this.setName("# ");
        this.setGender((byte) 0);
    }

    @Override
    public void update() {

    }

}
