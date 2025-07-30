package nro.server.engine.entity.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.model.ecs.component.player.QuestInstanceComponent;

/**
 * @author Arriety
 */
public class MovementSystem extends IteratingSystem {

    private ComponentMapper<PositionComponent> posMapper;
    private ComponentMapper<PlayerComponent> playerMapper;
    private ComponentMapper<QuestInstanceComponent> taskMapper;

    public MovementSystem() {
        super(Aspect.all(PositionComponent.class, PlayerComponent.class));
    }

    @Override
    protected void process(int entityId) {
        PositionComponent pos = posMapper.get(entityId);
        if (!pos.isDirty) return;

        System.out.println("entity di chuyen : " + entityId + " to " + pos.x + ", " + pos.y);
        if (pos.isOnGround == 1) {
//            player.getPoints().reduceMPWhenFlying();
        }

        pos.isDirty = false;
    }
}
