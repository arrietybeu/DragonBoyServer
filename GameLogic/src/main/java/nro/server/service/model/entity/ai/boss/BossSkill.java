package nro.server.service.model.entity.ai.boss;

import nro.consts.ConstSkill;
import nro.server.manager.skill.SkillManager;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.Skills;
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
            switch (this.skillSelect.getTemplate().getType()) {
                case ConstSkill.SKILL_FORCUS -> this.useSkillTarget(target);
                case ConstSkill.SKILL_SUPPORT -> { /* TODO */ }
                case ConstSkill.SKILL_NOT_FORCUS -> this.useSkillNotForcus();
            }
        } catch (Exception ex) {
            LogServer.LogException("useSkill player name:" + owner.getName() + " error: " + ex.getMessage());
        }
    }

}
