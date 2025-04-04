package nro.server.service.model.entity.ai.handler;

import nro.server.service.model.entity.ai.AIState;
import nro.server.service.model.entity.ai.AIStateHandler;
import nro.server.service.model.entity.ai.AbstractAI;
import nro.server.service.model.entity.player.Player;

public class AttackingEventHandler implements AIStateHandler {

    private static final int ATTACK_RANGE = 60;

    @Override
    public void handle(AbstractAI ai) {
        Player target = ai.getEntityTargetAsPlayer();

        // neu target mất hoặc chết hoặc khác map → về SEARCHING
        if (target == null || target.getPoints().isDead() || target.getArea() != ai.getArea()) {
            ai.setEntityTarget(null);
            ai.setState(AIState.SEARCHING);
            return;
        }

        int distance = Math.abs(ai.getX() - target.getX());

        // neu target quá xa → quay lại chase
        if (distance > ATTACK_RANGE) {
            ai.setState(AIState.CHASING);
            return;
        }

        // TODO: Kiểm tra cooldown tại đây (sẽ thêm sau)

        // Gọi kỹ năng đang chọn (hoặc bạn có thể random skill nếu muốn)
        ai.getSkills().useSkill(target);

        // Sau khi đánh, vẫn giữ ATTACKING hoặc chuyển về SEARCHING tùy ý bạn
        // ai.setStateIfNot(AIState.SEARCHING);
    }
}
