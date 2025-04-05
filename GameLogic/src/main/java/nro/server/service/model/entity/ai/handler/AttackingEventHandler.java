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

            System.out.println("AttackingEventHandler.handle: " + boss.getName() + " attacking target: " + (boss.getEntityTarget() != null ? boss.getEntityTarget().getName() : "null"));
            Player target = boss.getEntityTargetAsPlayer();

            // neu target mất hoặc chết hoặc khác map → về SEARCHING
            if (target == null || target.getPoints().isDead() || target.getArea() != ai.getArea()) {
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

            // TODO: Kiểm tra cooldown tại đây (sẽ thêm sau)

            // Gọi kỹ năng đang chọn (hoặc bạn có thể random skill nếu muốn)
            boss.getSkills().useSkill(target);

            // Sau khi đánh, vẫn giữ ATTACKING hoặc chuyển về SEARCHING tùy ý bạn
            // ai.setStateIfNot(AIState.SEARCHING);
        } catch (Exception e) {
            LogServer.LogException("AttackingEventHandler.handle: " + e.getMessage(), e);
        }
    }
}
