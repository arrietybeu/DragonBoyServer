package nro.server.model.world;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.artemis.utils.ImmutableBag;
import lombok.Getter;
import lombok.Setter;
import nro.server.engine.entity.GameWorld;
import nro.server.model.ecs.component.InfoComponent;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.ecs.component.player.PlayerComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private long lastEmptyTime = -1;
    private boolean scheduledForRemoval = false;

    private static final Logger log = LoggerFactory.getLogger(WorldMapInstance.class);

    public WorldMapInstance(WorldMap parent, byte instanceId) {
        this(parent, instanceId, -190606);
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
            if (entity == null)
                throw new IllegalArgumentException("Entity not found: " + id);
            GroupManager groupManager = GameWorld.getInstance().getGroupManager();
            groupManager.add(entity, groupName);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getEntityCount() {
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

    public com.artemis.utils.Bag<Entity> getPlayersInZone() {
        lock.readLock().lock();
        try {
            var world = GameWorld.getInstance().getWorld();
            ComponentMapper<PlayerComponent> mPlayer = world.getMapper(PlayerComponent.class);

            var gm = GameWorld.getInstance().getGroupManager();
            ImmutableBag<Entity> group = gm.getEntities(groupName);

            if (group == null) {
                throw new RuntimeException("Group not found: " + groupName);
            }

            var result = new com.artemis.utils.Bag<Entity>(group.size());

            for (int i = 0, n = group.size(); i < n; i++) {
                Entity e = group.get(i);
                if (mPlayer.has(e)) {
                    result.add(e);
                }
            }

//            System.out.println("Players in zone " + groupName + ": " + result.size());
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean removeEntity(int id) {
        lock.writeLock().lock();
        try {
            var world = GameWorld.getInstance().getWorld();
            Entity e = world.getEntity(id);
            GroupManager gm = GameWorld.getInstance().getGroupManager();

            ComponentMapper<PositionComponent> mPos = world.getMapper(PositionComponent.class);
            PositionComponent pos = mPos.get(e);
            if (pos != null && pos.getAreaId() != this.instanceId) {
                log.warn("removeEntity({}): areaId mismatch (entity area={}, this.instance={})",
                        id, pos.getAreaId(), this.instanceId);
                return true;
            }

            log.debug("Removing entity {} from group '{}'", id, groupName);

            gm.remove(e, groupName);

//            if (pos != null) {
//                pos.setAreaId(-1);  // sentinel cho "không thuộc instance nào"
//                // pos.mapId = 0; // nếu muốn reset map
//            }

            return true;
        } catch (Exception ex) {
            log.error("removeEntity({}) failed: {}", id, ex.getMessage(), ex);
            return false;
        } finally {
            lock.writeLock().unlock();
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
                    var info = e.getComponent(InfoComponent.class);
                    System.out.println("Player: " + info.name);
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
