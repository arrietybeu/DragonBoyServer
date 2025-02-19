package nro.model.template.skill;

import lombok.Getter;
import lombok.Setter;
import nro.model.template.entity.SkillInfo;

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

    public SkillInfo getSkill(short skillId, int level) {
        return this.skills.stream()
                .filter(skillInfo -> skillInfo.getTemplate().getId() == skillId && skillInfo.getSkillId() == level - 1)
                .findFirst()
                .orElse(null);
    }

}
