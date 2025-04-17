package nro.server.service.model.entity.player;

import nro.consts.ConstSkill;
import nro.server.service.core.player.PlayerService;
import nro.server.service.core.system.ServerService;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.Skills;
import nro.server.system.LogServer;

public class PlayerSkills extends Skills {

    public PlayerSkills(Entity entity) {
        super(entity);
    }

    @Override
    public Skills copy(Entity entity) {
        return null;
    }

    @Override
    public void useSkill(Entity target) {
        if (owner instanceof Player player) {
            try {
                if (this.skillSelect == null) return;
                if (!this.skillSelect.isReady()) return;

                // === TÍNH TOÁN MP TIÊU HAO ===
                long mpUse;
                int manaUseType = this.skillSelect.getTemplate().getManaUseType();
                long currentMp = player.getPoints().getCurrentMP();
                long maxMp = player.getPoints().getMaxMP();

                if (manaUseType == 2) {
                    mpUse = 1;
                } else if (manaUseType == 1) {
                    mpUse = this.skillSelect.getManaUse() * maxMp / 100;
                } else {
                    mpUse = this.skillSelect.getManaUse();
                }

                if (currentMp < mpUse) {
                    ServerService.getInstance().sendChatGlobal(player.getSession(), null, "Không đủ KI để sử dụng", false);
                    return;
                }

                player.getPoints().setCurrentMp(Math.max(0, currentMp - mpUse));
//                PlayerService.getInstance().sendMpForPlayer(player);

                // === START SKILL ===
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
    }

}
