package nro.server.engine.entity.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.model.ecs.component.player.QuestInstanceComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arriety
 */
public class MovementSystem extends IteratingSystem {

    private static final Logger log = LoggerFactory.getLogger(MovementSystem.class);
    private ComponentMapper<PositionComponent> posMapper;
    private ComponentMapper<PlayerComponent> playerMapper;
    private ComponentMapper<QuestInstanceComponent> taskMapper;

    public MovementSystem() {
        super(Aspect.all(PositionComponent.class, PlayerComponent.class));
    }

    @Override
    protected void process(int entityId) {
        try {
            handler(entityId);
        } catch (Throwable e) {
            PositionComponent pos = posMapper.get(entityId);
            if (pos != null) pos.isDirtyMove = false;
            log.error("MovementSystem error for entityId={}", entityId, e);
        }
    }

    private void handler(int entityId) {
        PositionComponent pos = posMapper.get(entityId);
        if (pos == null) return;

        if (!pos.isDirtyMove) return;

        pos.x = pos.newX;
        pos.y = pos.newY;
        pos.isOnGround = pos.isOnGroundNew;

//        System.out.println("entity di chuyen : " + entityId + " to " + pos.x + ", " + pos.y);
        if (pos.isOnGround == 1) {
//         player.getPoints().reduceMPWhenFlying();
        }

        log.info("entity di chuyen : {} to {}, {}", entityId, pos.x, pos.y);

        pos.isDirtyMove = false;
    }

}
