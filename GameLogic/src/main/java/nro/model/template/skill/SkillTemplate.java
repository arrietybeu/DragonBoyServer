package nro.model.template.skill;

import lombok.Data;
import nro.model.template.entity.SkillInfo;

import java.util.ArrayList;
import java.util.List;

@Data
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
}
