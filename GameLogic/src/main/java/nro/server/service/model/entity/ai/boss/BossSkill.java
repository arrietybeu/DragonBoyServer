package nro.server.service.model.entity.ai.boss;

import nro.consts.ConstSkill;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.skill.Skills;
import nro.server.service.model.skill.behavior.SkillBehaviorRegistry;
import nro.server.service.model.template.entity.SkillInfo;
import nro.server.system.LogServer;

public class BossSkill extends Skills {

    public BossSkill(Entity owner) {
        super(owner);
    }

    @Override
    public BossSkill copy(Entity entity) {
        BossSkill copy = new BossSkill(entity);
        for (SkillInfo skill : this.skills) {
            copy.addSkill(skill);
        }
        return copy;
    }

    @Override
    public void useSkill(Entity target) {
        try {
            if (skillSelect.getBehavior() == null) {
                skillSelect.setBehavior(SkillBehaviorRegistry.getBehavior(skillSelect.getSkillId()));
            }
            skillSelect.getBehavior().use(owner, target);
        } catch (Exception ex) {
            LogServer.LogException("Boss useSkill failed: " + ex.getMessage(), ex);
        }
    }

}
