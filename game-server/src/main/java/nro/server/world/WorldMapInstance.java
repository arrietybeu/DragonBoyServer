package nro.server.world;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import java.util.concurrent.locks.ReentrantReadWriteLock;

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

    private final Set<Integer> entities = new HashSet<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

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

    public void addEntity(int id) {
        if (entities.contains(id)) {
            throw new IllegalArgumentException("Entity with id " + id + " already exists in this instance.");
        }
        lock.writeLock().lock();
        try {
            entities.add(id);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to add entity with id " + id + " to instance " + instanceId, exception);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeEntity(int id) {
        lock.writeLock().lock();
        try {
            entities.remove(id);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to remove entity with id " + id + " from instance " + instanceId, exception);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getPlayerCount() {
        lock.readLock().lock();
        try {
            return entities.size();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to get players count from instance " + instanceId, exception);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Collection<Integer> getEntities() {
        lock.readLock().lock();
        try {
            return entities;
        } catch (Exception exception) {
            throw new RuntimeException("Failed to get entities from instance " + instanceId, exception);
        }
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
