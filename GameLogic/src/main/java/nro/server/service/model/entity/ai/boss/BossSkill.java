package nro.server.service.model.entity.ai.boss;

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
            SkillInfo skillInfo = SkillManager.getInstance()
                    .getSkillInfoByTemplateId(skill.getSkillId(), entity.getGender(), skill.getPoint());
            if (skillInfo != null) {
                copy.addSkill(skillInfo);
            } else {
                LogServer.LogException("BossSkill.copy(): skillInfo null for skillId=" + skill.getSkillId());
            }
        }
        return copy;
    }
}
