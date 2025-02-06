package nro.model.player;

import lombok.Data;
import lombok.Getter;
import nro.model.template.entity.SkillInfo;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlayerSkill {

    @Getter
    private List<SkillInfo> skills;
    private final Player player;

    private boolean isMonkey;

    public PlayerSkill(Player player) {
        this.player = player;
        this.skills = new ArrayList<>();
    }

    public void addSkill(SkillInfo skill) {
        this.skills.add(skill);
    }

    public void removeSkill(SkillInfo skill) {
        this.skills.remove(skill);
    }

}
