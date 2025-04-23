package nro.server.service.model.skill;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstPlayer;
import nro.consts.ConstSkill;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.monster.Monster;
import nro.server.service.model.skill.behavior.SkillBehaviorRegistry;
import nro.server.service.model.template.entity.SkillInfo;
import nro.server.system.LogServer;
import nro.commons.utils.Rnd;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuppressWarnings("ALL")
public abstract class Skills {

    protected List<SkillInfo> skills;
    protected final Entity owner;
    protected boolean isMonkey;
    protected byte[] skillShortCut;
    protected SkillInfo skillSelect;

    public Skills(Entity owner) {
        this.owner = owner;
        this.skills = new ArrayList<>();
    }

    public void entityAttackMonster(Monster monster) {
        try {
            if (monster == null) return;
            if (monster.getPoint().isDead()) return;
            this.useSkill(monster);
        } catch (Exception e) {
            LogServer.LogException("playerAttackMonster player name:" + owner.getName() + " error: " + e.getMessage(), e);
        }
    }

    public SkillInfo getSkillById(int id) {
        return this.skills.stream().filter(skillInfo -> skillInfo.getSkillId() == id).findFirst().orElse(null);
    }

    public SkillInfo getSkillByTemplateId(int id) {
        return this.skills.stream().filter(skillInfo -> skillInfo.getTemplate().getId() == id).findFirst().orElse(null);
    }

    public void selectSkill(int skillId) {
        if (skillSelect != null && skillSelect.getTemplate().getId() == skillId) return;
        try {
            for (SkillInfo skillInfo : this.skills) {
                if (skillInfo.getTemplate().getId() == -1) continue;
                if (skillInfo.getTemplate().getId() == skillId) {
                    if (skillInfo.getBehavior() == null) {
                        skillInfo.setBehavior(SkillBehaviorRegistry.getBehavior(skillId));
                    }
                    skillInfo.restoreBaseCooldown();
                    this.skillSelect = skillInfo;
                    break;
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("selectSkill player name:" + owner.getName() + " error: " + ex.getMessage(), ex);
        }
    }

    public SkillInfo getSkillDefaultByGender(int gender) {
        SkillInfo skillInfo;
        switch (gender) {
            case ConstPlayer.TRAI_DAT -> skillInfo = this.getSkillByTemplateId(ConstSkill.DRAGON);
            case ConstPlayer.NAMEC -> skillInfo = this.getSkillByTemplateId(ConstSkill.DEMON);
            case ConstPlayer.XAYDA -> skillInfo = this.getSkillByTemplateId(ConstSkill.GALICK);
            default -> throw new IllegalStateException("getSkillDefaultByGender Unexpected value: " + gender);
        }
        return skillInfo;
    }

    public void addSkill(SkillInfo skill) {
        this.skills.add(skill);
    }

    public void removeSkill(SkillInfo skill) {
        this.skills.remove(skill);
    }

    public int getSkillLevel(int skillTemplateId) {
        return this.skills.stream().filter(skillInfo -> skillInfo.getTemplate().getId() == skillTemplateId)
                .map(SkillInfo::getPoint)
                .findFirst()
                .orElse(-1);
    }

    // dành cho con boss
    public void selectRandomSkill() {
        if (!skills.isEmpty()) {
            SkillInfo selected = skills.get(Rnd.nextInt(0, skills.size()));
            if (selected.getBehavior() == null) {
                selected.setBehavior(SkillBehaviorRegistry.getBehavior(selected.getSkillId()));
            }
            this.skillSelect = selected;
        }
    }

    public int getTypSkill() {
        return switch (this.skillSelect.getTemplate().getType()) {
            case 1 -> 0;
            case 2 -> 1;
            default -> 0;
        };
    }

    public void dispose() {
        this.skills.clear();
        this.skills = null;
        this.skillSelect = null;
        this.skillShortCut = null;
    }

    public abstract void useSkill(Entity target);

    public abstract Skills copy(Entity entity);

}
