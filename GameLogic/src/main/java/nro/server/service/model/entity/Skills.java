package nro.server.service.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nro.consts.ConstPlayer;
import nro.consts.ConstSkill;
import nro.server.realtime.system.player.SkillSystem;
import nro.server.service.core.player.SkillService;
import nro.server.service.core.system.ServerService;
import nro.server.service.model.entity.ai.boss.Boss;
import nro.server.service.model.entity.monster.Monster;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.template.entity.SkillInfo;
import nro.server.system.LogServer;
import nro.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@SuppressWarnings("ALL")
public abstract class Skills {

    protected List<SkillInfo> skills;
    protected final Entity owner;
    protected boolean isMonkey;
    protected byte[] skillShortCut;
    protected SkillInfo skillSelect;

    public Skills(Entity owner) {
        this.owner = owner;
        this.skills = new ArrayList<>();
    }

    public void entityAttackMonster(Monster monster) {
        try {
            if (monster == null) return;
            if (monster.getPoint().isDead()) return;
            this.useSkill(monster);
        } catch (Exception e) {
            LogServer.LogException("playerAttackMonster player name:" + owner.getName() + " error: " + e.getMessage(), e);
        }
    }

    public void useSkill(Entity target) {
        try {
            if (!this.skillSelect.isReady()) return;

            switch (this.skillSelect.getTemplate().getType()) {
                case ConstSkill.SKILL_FORCUS -> this.useSkillTarget(target);
                case ConstSkill.SKILL_SUPPORT -> { /* TODO */ }
                case ConstSkill.SKILL_NOT_FORCUS -> this.useSkillNotForcus();
            }
            // tick đã dùng
            this.skillSelect.markUsedNow();
        } catch (Exception ex) {
            LogServer.LogException("useSkill player name:" + owner.getName() + " error: " + ex.getMessage());
        }
    }

    public void useSkillTarget(Entity target) {
        try {
            SkillService skillService = SkillService.getInstance();
            Entity owner = this.owner;
            switch (this.skillSelect.getTemplate().getId()) {
                case ConstSkill.DRAGON, ConstSkill.DEMON, ConstSkill.GALICK, ConstSkill.KAMEJOKO -> {
                    long dame = owner.getPoints().getDameAttack();
                    switch (target) {
                        case Player plTarget -> {
                            SkillService.getInstance().sendEntityAttackEntity(owner, plTarget, dame, true);
                            plTarget.handleAttack(owner, 0, dame);
                        }
                        case Monster monster -> monster.handleAttack(owner, 0, dame);
                        case Boss boss -> {
                            SkillService.getInstance().sendEntityAttackEntity(owner, boss, dame, true);
                            boss.handleAttack(owner, 0, dame);
                        }
                        default ->
                                LogServer.LogException("useSkillTarget player name:" + owner.getName() + " error: target not monster");
                    }
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("useSkillTarget player name:" + owner.getName() + " error: " + ex.getMessage(), ex);
        }
    }

    public void useSkillNotForcus() {
        try {
            switch (this.skillSelect.getTemplate().getId()) {
                case ConstSkill.TU_PHAT_NO -> {
                    SkillService.getInstance().sendUseSkillNotFocus(this.owner, 7, skillSelect.getSkillId(), 3000);
                    SkillSystem.getInstance().scheduleDelaySkill(3000, () -> {
                        SafeCallback.run(owner, () -> {
                            Map<Integer, Monster> monstersCopy = new HashMap<>(this.owner.getArea().getMonsters());
                            for (Monster monster : monstersCopy.values()) {
                                long dame = this.owner.getPoints().getMaxHP();
                                monster.handleAttack(this.owner, 1, dame);
                                SkillService.getInstance().sendEntityAttackMonster(this.owner, monster.getId());
                            }
                            owner.getPoints().setDie(owner);
                        });
                    });
                }
            }
        } catch (Exception exception) {
            LogServer.LogException("useSkillNotForcus player name:" + owner.getName() + " error: " + exception.getMessage(), exception);
        }
    }

    public SkillInfo getSkillById(int id) {
        return this.skills.stream().filter(skillInfo -> skillInfo.getSkillId() == id).findFirst().orElse(null);
    }

    public void selectSkill(int skillId) {
        if (skillSelect != null) {
            if (skillSelect.getTemplate().getId() == skillId) return;
        }
        try {
            for (SkillInfo skillInfo : this.skills) {
                if (skillInfo.getTemplate().getId() == -1) continue;
                if (skillInfo.getTemplate().getId() == skillId) {
                    this.skillSelect = skillInfo;
                    break;
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("selectSkill player name:" + owner.getName() + " error: " + ex.getMessage(), ex);
        }
    }

    public SkillInfo getSkillDefaultByGender(int gender) {
        SkillInfo skillInfo;
        switch (gender) {
            case ConstPlayer.TRAI_DAT -> skillInfo = this.getSkillById(ConstSkill.DRAGON);
            case ConstPlayer.NAMEC -> skillInfo = this.getSkillById(ConstSkill.DEMON);
            case ConstPlayer.XAYDA -> skillInfo = this.getSkillById(ConstSkill.GALICK);
            default -> throw new IllegalStateException("getSkillDefaultByGender Unexpected value: " + gender);
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
        return this.skills.stream().filter(skillInfo -> skillInfo.getTemplate().getId() == skillTemplateId)
                .map(SkillInfo::getPoint)
                .findFirst()
                .orElse(-1);
    }

    // dành cho con boss
    public void selectRandomSkill() {
        if (!skills.isEmpty()) {
            this.skillSelect = skills.get(Util.nextInt(0, skills.size()));
        }
    }

    public int getTypSkill() {
        return switch (this.skillSelect.getTemplate().getType()) {
            case 1 -> 0;
            case 2 -> 1;
            default -> 0;
        };
    }

    public void dispose() {
        this.skills.clear();
        this.skills = null;
        this.skillSelect = null;
        this.skillShortCut = null;
    }

    public abstract Skills copy(Entity entity);

}
