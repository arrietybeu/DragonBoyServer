package nro.model.player;

import lombok.Getter;
import lombok.Setter;
import nro.model.template.entity.SkillInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class PlayerSkill {

    private final List<SkillInfo> skills;
    private final Player player;

    private boolean isMonkey;
    private byte[] skillShortCut;

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

    @Override
    public String toString() {
        return "PlayerSkill{" +
                "skills=" + skills +
                ", player=" + player +
                ", isMonkey=" + isMonkey +
                ", skillShortCut=" + Arrays.toString(skillShortCut) +
                '}';
    }
}
