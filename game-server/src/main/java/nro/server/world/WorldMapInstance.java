package nro.server.world;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Arriety
 */
@Getter
@Setter
public class WorldMapInstance {

    private final int instanceId;
    private final WorldMap parent;
    private final int ownerId;
    private final long createTime;
    private final InstanceHandler handler;
    private final Map<Integer, Object> entities = new ConcurrentHashMap<>();

    public WorldMapInstance(WorldMap parent, int instanceId) {
        this(parent, instanceId, 0);
    }

    public WorldMapInstance(WorldMap parent, int instanceId, int ownerId) {
        this.parent = parent;
        this.instanceId = instanceId;
        this.ownerId = ownerId;
        this.createTime = System.currentTimeMillis();
        this.handler = new InstanceHandler(this);
    }

    public void addEntity(int id, Object entity) {
        entities.put(id, entity);
    }

    public void removeEntity(int id) {
        entities.remove(id);
    }

    public int getPlayerCount() {
        return entities.size();
    }

    public Map<Integer, Object> getEntities() {
        return entities;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public WorldMap getParent() {
        return parent;
    }

    public InstanceHandler getHandler() {
        return handler;
    }
}
