package nro.service.model.player;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nro.consts.ConstPlayer;
import nro.consts.ConstSkill;
import nro.service.model.LiveObject;
import nro.service.model.monster.Monster;
import nro.service.model.template.entity.SkillInfo;
import nro.server.LogServer;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@SuppressWarnings("ALL")
public class PlayerSkill {

    private final List<SkillInfo> skills;
    private final Player player;

    private boolean isMonkey;
    private byte[] skillShortCut;
    private SkillInfo skillSelect;

    public PlayerSkill(Player player) {
        this.player = player;
        this.skills = new ArrayList<>();
    }

    public void playerAttackMonster(Monster monster) {
        try {
            if (monster == null) return;
            if (monster.getPoint().isDead()) return;

            this.useSkill(monster);

        } catch (Exception e) {
            LogServer.LogException("playerAttackMonster player name:" + player.getName() + " error: " + e.getMessage(), e);
        }
    }

    private void useSkill(LiveObject target) {
        try {
            switch (this.skillSelect.getTemplate().getType()) {
                case ConstSkill.SKILL_FORCUS -> {
                    this.useSkillTarget(target);
                }
                case ConstSkill.SKILL_SUPPORT -> {
                }
                case ConstSkill.SKILL_NOT_FORCUS -> {
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("useSkill player name:" + player.getName() + " error: " + ex.getMessage());
        }
    }

    private void useSkillTarget(LiveObject target) {
        switch (this.skillSelect.getTemplate().getId()) {
            case ConstSkill.DRAGON, ConstSkill.DEMON, ConstSkill.GALICK -> {
                switch (target) {
                    case Player plTarget -> {
                    }
                    case Monster monster -> {
                        long dame = this.player.getPlayerPoints().getDameAttack();
                        monster.handleAttack(this.player, dame);
                    }
                    default ->
                            LogServer.LogException("useSkillTarget player name:" + player.getName() + " error: target not monster");
                }
            }
        }
    }

    public SkillInfo getSkillById(int id) {
        return this.skills.stream().filter(skillInfo -> skillInfo.getSkillId() == id).findFirst().orElse(null);
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
            LogServer.LogException("selectSkill player name:" + player.getName() + " error: " + ex.getMessage(), ex);
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

    public void addSkill(SkillInfo skill) {
        this.skills.add(skill);
    }

    public void removeSkill(SkillInfo skill) {
        this.skills.remove(skill);
    }

    public int getSkillLevel(int skillTemplateId) {
        for (SkillInfo skill : this.skills) {
            if (skill.getTemplate().getId() == skillTemplateId) {
                return skill.getPoint();
            }
        }
        return -1;
    }

}
