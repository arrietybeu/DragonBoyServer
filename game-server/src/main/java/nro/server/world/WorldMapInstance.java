package nro.server.world;

import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.artemis.utils.ImmutableBag;
import lombok.Getter;
import lombok.Setter;
import nro.server.engine.GameWorld;
import nro.server.model.ecs.component.player.PlayerComponent;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Arriety
 */
@Getter
@Setter
public class WorldMapInstance {

    private final byte instanceId;

    private final WorldMap parent;

    private final int ownerId;

    private final long createTime;

    private final InstanceHandler handler;

    private final String groupName;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public WorldMapInstance(WorldMap parent, byte instanceId) {
        this(parent, instanceId, 0);
    }

    public WorldMapInstance(WorldMap parent, byte instanceId, int ownerId) {
        this.parent = parent;
        this.instanceId = instanceId;
        this.ownerId = ownerId;
        this.createTime = System.currentTimeMillis();
        this.handler = new InstanceHandler(this);
        this.groupName = "map_" + parent.getId() + "_instance_" + instanceId;
    }

    public void addEntity(int id) {
        lock.writeLock().lock();
        try {
            var entity = GameWorld.getInstance().getWorld().getEntity(id);
            if (entity == null) {
                throw new IllegalArgumentException("Entity not found: " + id);
            }
            GroupManager groupManager = GameWorld.getInstance().getGroupManager();
            groupManager.add(entity, groupName);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeEntity(int id) {
        lock.writeLock().lock();
        try {
            var entity = GameWorld.getInstance().getWorld().getEntity(id);
            if (entity != null) {
                GroupManager groupManager = GameWorld.getInstance().getGroupManager();
                groupManager.remove(entity, groupName);
            }
//            if (getPlayerCount() == 0) {
//                parent.releaseInstance(instanceId); // Clear nếu empty
//            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getPlayerCount() {
        lock.readLock().lock();
        try {
            return getEntities().size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public ImmutableBag<Entity> getEntities() {
        lock.readLock().lock();
        try {
            GroupManager groupManager = GameWorld.getInstance().getGroupManager();
            ImmutableBag<Entity> entities = groupManager.getEntities(groupName);
            return entities != null ? entities : new com.artemis.utils.Bag<>();
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean isFullPlayer() {
        lock.readLock().lock();
        try {
            ImmutableBag<Entity> entities = this.getEntities();
            int playerCount = 0;
            int maxPlayer = parent.getTemplate().getMaxPlayer();

            for (int i = 0; i < entities.size(); i++) {
                Entity e = entities.get(i);
                if (e.getComponent(PlayerComponent.class) != null) {
                    playerCount++;
                }
            }

            return playerCount >= maxPlayer;
        } finally {
            lock.readLock().unlock();
        }
    }

    public byte getInstanceId() {
        return instanceId;
    }

    public WorldMap getParent() {
        return parent;
    }

    public InstanceHandler getHandler() {
        return handler;
    }
}
