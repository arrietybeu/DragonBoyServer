package nro.server.service.model.entity.player;

import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.Skills;

public class PlayerSkills extends Skills {

    public PlayerSkills(Entity entity) {
        super(entity);
    }

    @Override
    public Skills copy(Entity entity) {
        return null;
    }


}
