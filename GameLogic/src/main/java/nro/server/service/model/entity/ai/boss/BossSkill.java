package nro.server.service.model.entity.ai.boss;

import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.Skills;
import nro.server.service.model.entity.monster.Monster;
import nro.server.service.model.template.entity.SkillInfo;

public class BossSkill extends Skills {

    @Override
    public void entityAttackMonster(Monster monster) {

    }

    @Override
    public void useSkill(Entity target) {

    }

    @Override
    public void useSkillTarget(Entity target) {

    }

    @Override
    public void useSkillNotForcus() {

    }

    @Override
    public SkillInfo getSkillById(int id) {
        return null;
    }

    @Override
    public void selectSkill(int skillId) {

    }

    @Override
    public SkillInfo getSkillDefaultByGender(int gender) {
        return null;
    }

    @Override
    public void addSkill(SkillInfo skill) {

    }

    @Override
    public void removeSkill(SkillInfo skill) {

    }

    @Override
    public int getSkillLevel(int skillTemplateId) {
        return 0;
    }
}
