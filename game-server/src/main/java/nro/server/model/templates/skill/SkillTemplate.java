package nro.server.model.templates.skill;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arriety
 */

@Getter
@Setter
public class SkillTemplate {

    private byte id;
    private int classId;
    private String name;
    private byte maxPoint;
    private byte manaUseType;
    private byte type;
    private short iconId;
    private String description;
    private String damInfo;

    private List<SkillInfo> skills = new ArrayList<>();

    public void addSkill(SkillInfo skill) {
        this.skills.add(skill);
    }

}
