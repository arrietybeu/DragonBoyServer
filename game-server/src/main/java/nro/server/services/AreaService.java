package nro.server.services;


import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.world.World;


/**
 * @author Arriety
 */
public class AreaService {

    public void sendInfoAllLiveObjectsTo(Entity entity) {

        var position = entity.getComponent(PositionComponent.class);
        var areaInMap = World.getInstance().getAreaInMap(position.mapId, position.areaId);

        if (areaInMap == null) {
            throw new IllegalArgumentException("No area found for map ID " + position.mapId + " and area ID " + position.areaId);
        }

        ImmutableBag<Entity> entities = areaInMap.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if (e.getId() == entity.getId()) continue; // Skip self
            PlayerComponent playerComp = e.getComponent(PlayerComponent.class);
            if (playerComp != null) {

            }
        }
    }

    private static final class SingletonHolder {
        private static final AreaService INSTANCE = new AreaService();
    }

    public static AreaService getInstance() {
        return AreaService.SingletonHolder.INSTANCE;
    }

}
