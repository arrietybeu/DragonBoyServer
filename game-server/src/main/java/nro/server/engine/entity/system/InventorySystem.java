package nro.server.engine.entity.system;

import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nro.server.model.ecs.component.player.InventoryComponent;

/**
 * @author Arriety
 */
@All({InventoryComponent.class})
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class InventorySystem extends IteratingSystem {

    @Override
    protected void process(int entityId) {
    }
}
