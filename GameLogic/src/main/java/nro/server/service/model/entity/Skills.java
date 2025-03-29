package nro.server.service.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nro.server.service.model.entity.monster.Monster;
import nro.server.service.model.template.entity.SkillInfo;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@SuppressWarnings("ALL")
public abstract class Skills {

    protected final List<SkillInfo> skills;

    protected boolean isMonkey;
    protected byte[] skillShortCut;
    protected SkillInfo skillSelect;

    public Skills() {
        this.skills = new ArrayList<>();
    }

    public abstract void entityAttackMonster(Monster monster);

    public abstract void useSkill(Entity target);

    public abstract void useSkillTarget(Entity target);

    public abstract void useSkillNotForcus();

    public abstract SkillInfo getSkillById(int id);

    public abstract void selectSkill(int skillId);

    public abstract SkillInfo getSkillDefaultByGender(int gender);

    public abstract void addSkill(SkillInfo skill);

    public abstract void removeSkill(SkillInfo skill);

    public abstract int getSkillLevel(int skillTemplateId);

}
