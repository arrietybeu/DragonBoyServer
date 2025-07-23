package nro.server.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import nro.server.model.ecs.component.*;
import nro.server.model.ecs.component.player.InventoryComponent;

/**
 * @author Arriety
 */
public class HealthSystem extends IteratingSystem {
    private ComponentMapper<HealthComponent> healthMapper;
    private ComponentMapper<StatsComponent> statsMapper;
    private ComponentMapper<InventoryComponent> inventoryMapper;

    public HealthSystem() {
        super(Aspect.all(HealthComponent.class));
    }

    @Override
    protected void process(int entityId) {
        HealthComponent health = healthMapper.get(entityId);
        if (!health.isDirty) return;

        StatsComponent stats = statsMapper.get(entityId);
        InventoryComponent inventory = inventoryMapper.get(entityId);
    }

}
