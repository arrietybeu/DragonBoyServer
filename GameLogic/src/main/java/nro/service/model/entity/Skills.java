package nro.service.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nro.consts.ConstPlayer;
import nro.consts.ConstSkill;
import nro.service.core.player.SkillService;
import nro.service.model.entity.boss.Boss;
import nro.service.model.entity.discpile.Disciple;
import nro.service.model.entity.monster.Monster;
import nro.service.model.entity.player.Player;
import nro.service.model.template.entity.SkillInfo;
import nro.server.system.LogServer;
import nro.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@SuppressWarnings("ALL")
public class Skills {

    private final List<SkillInfo> skills;

    private final BaseModel entity;

    private Player player;
    private Boss boss;
    private Disciple disciple;

    private boolean isMonkey;
    private byte[] skillShortCut;
    private SkillInfo skillSelect;

    public Skills(BaseModel entity) {
        this.entity = entity;
        this.skills = new ArrayList<>();
        this.setEntity(this.entity);
    }

    private void setEntity(BaseModel entity) {
        switch (entity) {
            case Player player -> this.player = player;
            case Boss boss -> this.boss = boss;
            case Disciple disciple -> this.disciple = disciple;
            default ->
                    LogServer.LogException("setEntity player name:" + player.getName() + " error: entity not player");
        }
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

    public void useSkill(BaseModel target) {
        try {
            switch (this.skillSelect.getTemplate().getType()) {
                case ConstSkill.SKILL_FORCUS -> {
                    this.useSkillTarget(target);
                }
                case ConstSkill.SKILL_SUPPORT -> {
                }
                case ConstSkill.SKILL_NOT_FORCUS -> {
                    this.useSkillNotForcus();
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("useSkill player name:" + player.getName() + " error: " + ex.getMessage());
        }
    }

    private void useSkillTarget(BaseModel target) {
        switch (this.skillSelect.getTemplate().getId()) {
            case ConstSkill.DRAGON, ConstSkill.DEMON, ConstSkill.GALICK -> {
                switch (target) {
                    case Player plTarget -> {
                    }
                    case Monster monster -> {
                        long dame = this.player.getPoints().getDameAttack();
                        monster.handleAttack(this.player, 0, dame);
                    }
                    default ->
                            LogServer.LogException("useSkillTarget player name:" + player.getName() + " error: target not monster");
                }
            }
        }
    }

    private void useSkillNotForcus() {
        switch (this.skillSelect.getTemplate().getId()) {
            case ConstSkill.TU_PHAT_NO -> {
                SkillService.getInstance().sendUseSkillNotFocus(this.player, 7, skillSelect.getSkillId(), 3000);
                Util.delay(3, () -> {
                    Map<Integer, Monster> monstersCopy = new HashMap<>(this.player.getArea().getMonsters());
                    for (Monster monster : monstersCopy.values()) {
                        long dame = this.player.getPoints().getMaxHP();
                        monster.handleAttack(this.player, 1, dame);
                        player.getPoints().setDie();
                    }
                });
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
