package nro.server.world;

import lombok.Getter;
import nro.server.model.templates.world.WorldMapTemplate;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Arriety
 */
public class WorldMap {

    @Getter
    private final int id;
    @Getter
    private final String name;
    @Getter
    private final WorldMapTemplate template;

    private final Map<Integer, WorldMapInstance> instances = new ConcurrentHashMap<>();

    public WorldMap(WorldMapTemplate template) {
        this.id = template.getId();
        this.name = template.getName();
        this.template = template;

        WorldMapInstance defaultInstance = new WorldMapInstance(this, 0);
        instances.put(0, defaultInstance);
    }

    public WorldMapInstance getInstance(int instanceId) {
        return instances.get(instanceId);
    }

    public Collection<WorldMapInstance> getAllInstances() {
        return instances.values();
    }

    public WorldMapInstance createNewInstance() {
        int nextId = generateNextInstanceId();
        WorldMapInstance instance = new WorldMapInstance(this, nextId);
        instances.put(nextId, instance);
        return instance;
    }

    private int generateNextInstanceId() {
        int id = 1;
        while (instances.containsKey(id)) id++;
        return id;
    }
}
