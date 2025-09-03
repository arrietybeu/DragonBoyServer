package nro.server.engine.entity.system;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import nro.server.model.ecs.component.player.InventoryComponent;

/**
 * @author Arriety
 */
public class InventorySystem extends IteratingSystem {

    public InventorySystem() {
        super(Aspect.all(InventoryComponent.class));
    }

    @Override
    protected void process(int entityId) {
    }
}
