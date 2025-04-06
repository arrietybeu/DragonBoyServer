package nro.server.service.model.entity.ai.handler;

import nro.server.service.model.entity.ai.AIState;
import nro.server.service.model.entity.ai.AIStateHandler;
import nro.server.service.model.entity.ai.AbstractAI;
import nro.server.service.model.entity.ai.boss.Boss;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;

public class AttackingEventHandler implements AIStateHandler {

    private static final int ATTACK_RANGE = 60;

    @Override
    public void handle(AbstractAI ai) {
        try {
            Boss boss = (Boss) ai;
            if (boss == null) return;

                Player target = boss.getEntityTargetAsPlayer();

            if (target == null) {
                if (boss.isAutoDespawn()) {
                    boss.dispose();
                    return;
                }
            }
            // neu target mất hoặc chết hoặc khác map → về SEARCHING
            if (ai.isValidTarget(target)) {
                boss.setEntityTarget(null);
                boss.setState(AIState.SEARCHING);
                return;
            }

            int distance = Math.abs(boss.getX() - target.getX());

            // neu target quá xa → quay lại chase
            if (distance > ATTACK_RANGE) {
                boss.setState(AIState.CHASING);
                return;
            }

            // Gọi kỹ năng đang chọn (hoặc bạn có thể random skill nếu muốn)
            if (boss.getSkills() == null) {
                return;
            }

            boss.getSkills().selectRandomSkill();

            if (boss.getSkills().getSkillSelect() == null) {
                return;
            }

            boss.getSkills().useSkill(target);

            // Sau khi đánh, vẫn giữ ATTACKING hoặc chuyển về SEARCHING tùy ý bạn
            // ai.setStateIfNot(AIState.SEARCHING);
        } catch (Exception e) {
            LogServer.LogException("AttackingEventHandler.handle: " + e.getMessage(), e);
        }
    }


}
