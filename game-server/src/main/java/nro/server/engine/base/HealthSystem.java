package nro.server.engine.base;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import nro.server.model.ecs.component.*;
import nro.server.model.ecs.component.player.InventoryComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arriety
 */
public final class HealthSystem extends IteratingSystem {

    // TODO system này dùng để xử lý các thay đổi về máu, mp, trạng thái chết sống, hồi sinh, v.v.
    private static final Logger log = LoggerFactory.getLogger(HealthSystem.class);

    private ComponentMapper<HealthComponent> healthMapper;
    private ComponentMapper<StatsComponent> statsMapper;
    private ComponentMapper<InventoryComponent> inventoryMapper;

    public HealthSystem() {
        super(Aspect.all(HealthComponent.class));
    }

    @Override
    protected void process(int entityId) {
        try {
            handler(entityId);
        } catch (Throwable throwable) {
            HealthComponent health = healthMapper.get(entityId);
            if (health != null) health.isDirty = false;
            log.error("HealthSystem error for entityId={}", entityId, throwable);
        }
    }

    private void handler(int entityId) {
        HealthComponent health = healthMapper.get(entityId);
        if (!health.isDirty) return;

        StatsComponent stats = statsMapper.get(entityId);
        InventoryComponent inventory = inventoryMapper.get(entityId);
    }


}
