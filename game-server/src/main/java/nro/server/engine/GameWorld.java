package nro.server.engine;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.GroupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Singleton quản lý ECS World, hỗ trợ virtual threads (JDK 21) và ReentrantLock.
 *
 * @author Arriety
 */
public class GameWorld {

    private static final Logger log = LoggerFactory.getLogger(GameWorld.class);

    private World world;
    private volatile boolean running = false;
    private ExecutorService executor;
    private int activeEntityCount = 0;
    private final long tickInterval;
    private final ReentrantLock lock = new ReentrantLock();

    private GameWorld() {
        int ticksPerSecond = 60;
        this.tickInterval = 1000 / ticksPerSecond;
    }

    public void initialize(WorldConfigurationBuilder builder) {
        lock.lock();
        try {
            if (this.world != null) {
                log.warn("GameWorld has already been initialized.");
                return;
            }
            WorldConfiguration config = builder.build();
            this.world = new World(config);
            log.info("Artemis-odb World initialized successfully.");
        } finally {
            lock.unlock();
        }
    }

    public void expandEntityCapacity(int targetCapacity) {
        lock.lock();
        try {
            log.debug("Expanding entity capacity to {}", targetCapacity);
            int[] dummyIds = new int[targetCapacity];
            for (int i = 0; i < targetCapacity; i++) {
                dummyIds[i] = world.create();
            }
            for (int id : dummyIds) {
                world.delete(id);
            }
            log.debug("Entity capacity expanded, current active entities: {}",
                    getEntityCount());
        } finally {
            lock.unlock();
        }
    }

    public void start() {
        lock.lock();
        try {
            if (world == null) {
                log.error("World is not initialized. Cannot start GameLoop.");
                return;
            }
            if (running) {
                log.warn("GameLoop is already running.");
                return;
            }

            running = true;
            executor = Executors.newVirtualThreadPerTaskExecutor();
            executor.submit(this::runGameLoop);
            log.info("GameLoop started with {} systems.", world.getSystems().size());
        } finally {
            lock.unlock();
        }
    }

    private void runGameLoop() {
        long lastTick = System.currentTimeMillis();
        while (running) {
            try {
                long now = System.currentTimeMillis();
                long elapsed = now - lastTick;

                if (elapsed >= tickInterval) {
                    world.setDelta(elapsed / 1000.0f);
                    world.process();
                    lastTick = now;
                } else {
                    Thread.sleep(tickInterval - elapsed); // Sleep vẫn OK, virtual thread không block real thread
                }
            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();
                log.warn("GameWorld thread interrupted.");
            } catch (Exception e) {
                log.error("Exception in GameWorld loop", e);
            }
        }
    }

    public void shutdown() {
        running = false;
        lock.lock();
        try {
            if (executor != null) {
                executor.shutdown();
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            }
            if (world != null) {
                world.dispose();
            }
            log.info("GameWorld has been shut down.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while shutting down.", e);
        } finally {
            lock.unlock();
        }
    }

    public World getWorld() {
        return this.world;
    }

    /**
     * Tạo entity mới, thêm Component nếu cần.
     *
     * @return Entity ID hoặc -1 nếu lỗi.
     */
    public int createEntity() {
        lock.lock();
        try {
            if (world == null) {
                log.error("World not initialized, cannot create entity.");
                return -1;
            }
            Entity entity = world.createEntity();
            activeEntityCount++; // Tăng counter
            log.info("Created entity ID: {}, Active count: {}", entity.getId(), activeEntityCount);
            return entity.getId();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Xóa entity và Component liên quan.
     */
    public void deleteEntity(int entityId) {
        lock.lock();
        try {
            if (world == null) {
                log.error("World not initialized, cannot delete entity.");
                return;
            }
            if (world.getEntity(entityId) != null) {
                world.delete(entityId);
                activeEntityCount--; // Giảm counter
                log.info("Deleted entity ID: {}, Active count: {}", entityId, activeEntityCount);
            } else {
                log.warn("Entity ID {} not found, cannot delete.", entityId);
            }
        } finally {
            lock.unlock();
        }
    }

    public int getEntityCount() {
        lock.lock();
        try {
            if (world == null) {
                log.error("World not initialized, cannot get entity count.");
                return 0;
            }
            log.info("Current active entities: {}", activeEntityCount);
            return activeEntityCount;
        } finally {
            lock.unlock();
        }
    }

    public GroupManager getGroupManager() {
        return world.getSystem(GroupManager.class);
    }

    private static class SingletonHolder {
        private static final GameWorld instance = new GameWorld();
    }

    public static GameWorld getInstance() {
        return SingletonHolder.instance;
    }
}