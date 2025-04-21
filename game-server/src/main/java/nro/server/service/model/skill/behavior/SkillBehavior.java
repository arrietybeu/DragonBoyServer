package nro.server.service.model.skill.behavior;

import nro.server.service.model.entity.Entity;

public interface SkillBehavior {

    void use(Entity caster, Entity target);

}