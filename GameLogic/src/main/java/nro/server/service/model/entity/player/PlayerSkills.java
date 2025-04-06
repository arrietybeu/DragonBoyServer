package nro.server.service.model.entity.player;

import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.Skills;
import nro.server.service.model.entity.monster.Monster;
import nro.server.service.model.template.entity.SkillInfo;


public class PlayerSkills extends Skills {

    public PlayerSkills(Entity entity) {
        super(entity);
    }

    @Override
    public void entityAttackMonster(Monster monster) {
        super.entityAttackMonster(monster);
    }

    @Override
    public void useSkill(Entity target) {
        super.useSkill(target);
    }

    @Override
    public void useSkillTarget(Entity target) {
        super.useSkillTarget(target);
    }

    @Override
    public void useSkillNotForcus() {
        super.useSkillNotForcus();
    }

    @Override
    public SkillInfo getSkillById(int id) {
        return super.getSkillById(id);
    }

    @Override
    public void selectSkill(int skillId) {
        super.selectSkill(skillId);
    }

    @Override
    public SkillInfo getSkillDefaultByGender(int gender) {
        return super.getSkillDefaultByGender(gender);
    }

    @Override
    public void addSkill(SkillInfo skill) {
        super.addSkill(skill);
    }

    @Override
    public void removeSkill(SkillInfo skill) {
        super.removeSkill(skill);
    }

    @Override
    public int getSkillLevel(int skillTemplateId) {
        return super.getSkillLevel(skillTemplateId);
    }


}
