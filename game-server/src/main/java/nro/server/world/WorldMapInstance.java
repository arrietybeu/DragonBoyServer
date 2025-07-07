package nro.server.world;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Arriety
 */
public class WorldMapInstance {

    @Getter
    private final int instanceId;
    @Getter
    private final WorldMap parent;

    private final Map<Integer, Object> entities = new ConcurrentHashMap<>();

    public WorldMapInstance(WorldMap parent, int instanceId) {
        this.parent = parent;
        this.instanceId = instanceId;
    }

    public void addEntity(int id, Object entity) {
        entities.put(id, entity);
    }

    public void removeEntity(int id) {
        entities.remove(id);
    }

    public Map<Integer, Object> getEntities() {
        return entities;
    }
}
