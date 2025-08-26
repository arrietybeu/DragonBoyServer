package nro.server.utils;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import nro.server.engine.entity.GameWorld;

/**
 * @author Arriety
 */
public final class PlayerEntityHelper {

    private static World getWorld() {
        return GameWorld.getInstance().getWorld();
    }

    public static Entity getEntity(int entityId) {
        Entity entity = getWorld().getEntity(entityId);
        if (entity == null) {
            throw new IllegalArgumentException("Entity not found: " + entityId);
        }
        return entity;
    }

    public static <T extends Component> T getComponent(int entityId, Class<T> componentClass) {
        return getEntity(entityId).getComponent(componentClass);
    }

}
