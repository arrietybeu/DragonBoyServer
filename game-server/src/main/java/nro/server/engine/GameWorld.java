package nro.server.engine;

import com.artemis.*;
import com.artemis.managers.GroupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;
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
            } else {
                log.warn("Entity ID {} not found, cannot delete.", entityId);
            }
        } finally {
            lock.unlock();
        }
    }

    public void logWorldSummary() {
        if (world == null) {
            log.warn("World is null, cannot log summary.");
            return;
        }

        var em = world.getEntityManager();
        var cm = world.getComponentManager();

        log.info("======= ECS World Summary =======");

        // Log tổng số entity đang hoạt động (theo cách hợp lệ)
        int activeEntities = world.getAspectSubscriptionManager()
                .get(Aspect.all())  // Aspect.all() sẽ match mọi entity
                .getActiveEntityIds()
                .cardinality();
        log.info("Total entities (active): {}", activeEntities);
        log.info("Next entity ID: {}", getNextEntityId(em));

        // Log các Component Type đã đăng ký
        var componentTypes = cm.getComponentTypes();
        log.info("Registered component types: {}", componentTypes.size());
        for (var type : componentTypes) {
            log.info(" - Component: {} [id={}]",
                    type.getType().getSimpleName(),
                    type.getIndex()
            );
        }

        // Log các hệ thống đang được active
        log.info("Active systems ({}):", world.getSystems().size());
        for (var system : world.getSystems()) {
            log.info(" - {}", system.getClass().getSimpleName());
        }

        // Log các group đang tồn tại
        GroupManager gm = getGroupManager();
        if (gm != null) {
            log.info("GroupManager groups:");
            var field = getPrivateField(gm.getClass(), "entitiesByGroup");
            if (field != null) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, ?> map = (Map<String, ?>) field.get(gm);
                    for (Map.Entry<String, ?> entry : map.entrySet()) {
                        String group = entry.getKey();
                        Object bag = entry.getValue();
                        int size = bag instanceof com.artemis.utils.Bag
                                ? ((com.artemis.utils.Bag<?>) bag).size()
                                : -1;
                        log.info(" - Group '{}' has {} entities", group, size);
                    }
                } catch (IllegalAccessException e) {
                    log.error("Failed to access entitiesByGroup", e);
                }
            }
        }

        log.info("==================================");
    }

    private Field getPrivateField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            log.warn("Field {} not found in class {}", name, clazz.getSimpleName());
            return null;
        }
    }

    private int getNextEntityId(EntityManager em) {
        try {
            var field = EntityManager.class.getDeclaredField("nextId");
            field.setAccessible(true);
            return field.getInt(em);
        } catch (Exception e) {
            log.warn("Failed to access nextId from EntityManager", e);
            return -1;
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