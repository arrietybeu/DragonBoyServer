package nro.server.service.model.skill.handler;

import nro.consts.ConstSkill;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.skill.behavior.ASkillHandler;
import nro.server.service.model.skill.behavior.SkillBehavior;

@ASkillHandler({ConstSkill.DRAGON, ConstSkill.DEMON, ConstSkill.GALICK, ConstSkill.KAMEJOKO})
public class MeleeBlastSkill implements SkillBehavior {

    @Override
    public void use(Entity caster, Entity target) {
        long damage = caster.getPoints().getDameAttack(target);
        target.handleAttack(caster, 0, damage);
    }

}
