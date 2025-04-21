package nro.server.service.model.entity.player;

import nro.server.service.core.player.PlayerService;
import nro.server.service.core.system.ServerService;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.skill.Skills;
import nro.server.service.model.skill.behavior.SkillBehavior;
import nro.server.service.model.template.entity.SkillInfo;
import nro.server.system.LogServer;

public class PlayerSkills extends Skills {

    public PlayerSkills(Entity entity) {
        super(entity);
    }

    @Override
    public Skills copy(Entity entity) {
        PlayerSkills copied = new PlayerSkills(entity);
        for (SkillInfo skill : this.skills) {
            copied.addSkill(skill.copy());
        }
        return copied;
    }

    @Override
    public void useSkill(Entity target) {
        try {
            if (this.skillSelect == null) return;
            if (!this.skillSelect.isReady()) return;

            if (!hasEnoughMp(skillSelect)) {
                notifyNotEnoughMp();
                return;
            }

            consumeMp(skillSelect);

            SkillBehavior behavior = skillSelect.getBehavior();
            if (behavior != null) {
                behavior.use(owner, target);
                this.skillSelect.markUsedNow();
            } else {
                LogServer.LogException("SkillBehavior null for skill: " + skillSelect.getSkillId());
            }
        } catch (Exception exception) {
            LogServer.LogException("PlayerSkill: useSkill: " + exception.getMessage(), exception);
        }
    }

    private boolean hasEnoughMp(SkillInfo skill) {
        long currentMp = owner.getPoints().getCurrentMP();
        long maxMp = owner.getPoints().getMaxMP();
        return currentMp >= calculateMpUse(skill, maxMp);
    }

    private void consumeMp(SkillInfo skill) {
        long currentMp = owner.getPoints().getCurrentMP();
        long maxMp = owner.getPoints().getMaxMP();
        long mpUse = calculateMpUse(skill, maxMp);
        owner.getPoints().setCurrentMp(Math.max(0, currentMp - mpUse));
    }

    private long calculateMpUse(SkillInfo skill, long maxMp) {
        return switch (skill.getTemplate().getManaUseType()) {
            case 2 -> 1;
            case 1 -> skill.getManaUse() * maxMp / 100;
            default -> skill.getManaUse();
        };
    }

    private void notifyNotEnoughMp() {
        if (owner instanceof Player player) {
            PlayerService.getInstance().sendMpForPlayer(player);
            ServerService.getInstance().sendChatGlobal(player.getSession(), null, "Không đủ KI để sử dụng", false);
        }
    }
}