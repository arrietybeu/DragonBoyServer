package nro.service.model.entity.player;

import nro.consts.ConstPlayer;
import nro.consts.ConstSkill;
import nro.server.system.LogServer;
import nro.service.core.player.SkillService;
import nro.service.core.system.ServerService;
import nro.service.model.entity.BaseModel;
import nro.service.model.entity.Skills;
import nro.service.model.entity.monster.Monster;
import nro.service.model.template.entity.SkillInfo;
import nro.utils.Util;

import java.util.HashMap;
import java.util.Map;

public class PlayerSkills extends Skills {

    private final Player player;

    public PlayerSkills(Player player) {
        this.player = player;
    }

    @Override
    public void entityAttackMonster(Monster monster) {
        try {
            if (monster == null) return;
            if (monster.getPoint().isDead()) return;

            this.useSkill(monster);

        } catch (Exception e) {
            LogServer.LogException("playerAttackMonster player name:" + player.getName() + " error: " + e.getMessage(), e);
        }
    }

    @Override
    public void useSkill(BaseModel target) {
        try {
            if (!this.skillSelect.isReady()) {
                // Gửi thông báo cho client biết đang hồi chiêu
                var notify = "Kỹ năng đang hồi chiêu: " + skillSelect.getCooldownRemaining() + "ms";
                ServerService.getInstance().sendChatGlobal(this.player.getSession(), null, notify, false);
                return;
            }

            // Đánh dấu đã dùng
            this.skillSelect.markUsedNow();

            switch (this.skillSelect.getTemplate().getType()) {
                case ConstSkill.SKILL_FORCUS -> this.useSkillTarget(target);
                case ConstSkill.SKILL_SUPPORT -> { /* TODO */ }
                case ConstSkill.SKILL_NOT_FORCUS -> this.useSkillNotForcus();
            }
        } catch (Exception ex) {
            LogServer.LogException("useSkill player name:" + player.getName() + " error: " + ex.getMessage());
        }
    }

    @Override
    public void useSkillTarget(BaseModel target) {
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

    @Override
    public void useSkillNotForcus() {
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

    @Override
    public SkillInfo getSkillById(int id) {
        return this.skills.stream().filter(skillInfo -> skillInfo.getSkillId() == id).findFirst().orElse(null);
    }

    @Override
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

    @Override
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
    public void addSkill(SkillInfo skill) {
        this.skills.add(skill);
    }

    @Override
    public void removeSkill(SkillInfo skill) {
        this.skills.remove(skill);
    }

    @Override
    public int getSkillLevel(int skillTemplateId) {
        for (SkillInfo skill : this.skills) {
            if (skill.getTemplate().getId() == skillTemplateId) {
                return skill.getPoint();
            }
        }
        return -1;
    }


}
