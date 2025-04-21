package nro.server.service.model.template.skill;

import lombok.Getter;
import lombok.Setter;
import nro.server.service.model.template.entity.SkillInfo;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SkillTemplate {

    private byte id;
    private int classId;
    private String name;
    private int maxPoint;
    private int manaUseType;
    private int type;
    private int iconId;
    private String description;
    private String damInfo;

    private List<SkillInfo> skills = new ArrayList<>();

    public void addSkill(SkillInfo skill) {
        this.skills.add(skill);
    }

    public SkillInfo getSkillByTemplateId(short skillId, int level) {
        for (SkillInfo skillInfo : this.skills) {
            if (skillInfo.getTemplate().getId() == skillId && skillInfo.getPoint() == level) {
                return skillInfo;
            }
        }
        return null;
    }

    public SkillInfo getSkillById(int skillId) {
        for (SkillInfo skillInfo : this.skills) {
            if (skillInfo.getSkillId() == skillId) {
                return skillInfo;
            }
        }
        return null;
    }

}
