package nro.model.player;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstPlayer;
import nro.consts.ConstSkill;
import nro.model.template.entity.SkillInfo;
import nro.server.LogServer;

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
    public SkillInfo skillSelect;

    public PlayerSkill(Player player) {
        this.player = player;
        this.skills = new ArrayList<>();
    }

    public void addSkill(SkillInfo skill) {
        this.skills.add(skill);
    }

    public SkillInfo getSkillById(int id) {
        for (SkillInfo skillInfo : this.skills) {
            if (skillInfo.getSkillId() == id) {
                return skillInfo;
            }
        }
        return null;
    }

    public void removeSkill(SkillInfo skill) {
        this.skills.remove(skill);
    }

    public void selectSkill(int skillId) {
        try {
            for (SkillInfo skillInfo : this.skills) {
                if (skillInfo.getTemplate().getId() == -1) continue;
                if (skillInfo.getTemplate().getId() == skillId) {
                    this.skillSelect = skillInfo;
                    break;
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("selectSkill player name:" + player.getName() + " error: " + ex.getMessage());
        }
    }

    public SkillInfo getSkillDefaultByGender(int gender) {
        SkillInfo skillInfo;
        switch (gender) {
            case ConstPlayer.TRAI_DAT: {
                skillInfo = this.getSkillById(ConstSkill.DRAGON);
                break;
            }
            case ConstPlayer.NAMEC: {
                skillInfo = this.getSkillById(ConstSkill.DEMON);
                break;
            }
            case ConstPlayer.XAYDA: {
                skillInfo = this.getSkillById(ConstSkill.GALICK);
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + gender);
        }
        return skillInfo;
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
