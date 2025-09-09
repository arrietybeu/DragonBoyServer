package nro.server.engine;

import com.artemis.*;
import com.artemis.managers.GroupManager;
import com.artemis.utils.Bag;
import com.artemis.utils.BitVector;

import nro.server.engine.profiling.ProfilingInvocationStrategy;
import nro.server.services.EntityQueryService;
import nro.server.utils.ThreadPoolManager;
import nro.commons.utils.concurrent.NamedRunnable;
import nro.server.configs.main.ThreadConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Arriety
 */
public final class GameWorld {

    private static final Logger log = LoggerFactory.getLogger(GameWorld.class);

    private World world;
    private volatile boolean running = false;
    private ScheduledFuture<?> executor;
    private final long tickInterval;
    private final ReentrantLock lock = new ReentrantLock();
    private final ConcurrentLinkedQueue<Runnable> mainQueue = new ConcurrentLinkedQueue<>();
    private volatile long lastTickMs = System.currentTimeMillis();
    public static EntityQueryService queryService;

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

            config.setInvocationStrategy(new ProfilingInvocationStrategy(ThreadConfig.MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING));

            this.world = new World(config);
            queryService = new EntityQueryService(world);
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
            lastTickMs = System.currentTimeMillis();
            executor = ThreadPoolManager.getInstance().scheduleAtFixedRate(
                    new NamedRunnable("GameWorld.runGameLoop", this::runGameLoop), 0, tickInterval);
            log.info("GameLoop started with {} systems.", world.getSystems().size());
        } finally {
            lock.unlock();
        }
    }

    private void runGameLoop() {
        if (!running)
            return;
        long now = System.currentTimeMillis();
        long elapsed = Math.max(0, now - lastTickMs);

        lock.lock();
        try {
            for (Runnable r; (r = mainQueue.poll()) != null;) {
                try {
                    r.run();
                } catch (Throwable t) {
                    log.error("Error executing task on main thread", t);
                }
            }

            world.setDelta(elapsed / 1000.0f);
            world.process();
        } catch (Exception e) {
            log.error("Exception in GameWorld tick", e);
        } finally {
            lastTickMs = now;
            lock.unlock();
        }
    }

    public void shutdown() {
        running = false;
        lock.lock();
        try {
            if (executor != null) {
                executor.cancel(false);
            }
            if (world != null) {
                world.dispose();
            }
            log.info("GameWorld has been shut down.");
        } catch (Exception e) {
            log.error("Error while shutting down.", e);
        } finally {
            lock.unlock();
        }
    }

    public World getWorld() {
        if (this.world == null)
            throw new IllegalStateException("GameWorld is not initialized. Call initialize() first.");
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
        lock.lock();
        if (world == null) {
            log.warn("World is null, cannot log summary.");
            lock.unlock();
            return;
        }

        var em = world.getEntityManager();
        var cm = world.getComponentManager();

        log.info("======= ECS World Summary =======");

        // Log tổng số entity đang hoạt động (theo cách hợp lệ)
        int activeEntities = world.getAspectSubscriptionManager()
                .get(Aspect.all()) // Aspect.all() sẽ match mọi entity
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
                    type.getIndex());
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
        lock.unlock();
    }

    public void logEntitiesWithComponentsJson(int maxEntitiesToShow) {
        lock.lock();
        if (world == null) {
            log.warn("World is null, cannot dump entities.");
            lock.unlock();
            return;
        }

        AspectSubscriptionManager asm = world.getAspectSubscriptionManager();
        EntitySubscription subAll = asm.get(Aspect.all());
        BitVector activeIds = subAll.getActiveEntityIds();

        int total = activeIds.cardinality();
        int shown = Math.min(total, maxEntitiesToShow);

        log.info("=== Active Entities Dump (count={}, shown={}) ===", total, shown);

        ComponentManager cm = world.getComponentManager();
        Bag<Component> bag = new Bag<>(16);

        int count = 0;
        for (int entityId = activeIds.nextSetBit(0); entityId >= 0
                && count < shown; entityId = activeIds.nextSetBit(entityId + 1), count++) {

            bag.clear();
            cm.getComponentsFor(entityId, bag);

            String json = componentsToJsonArray(bag);
            log.info(" - {}: {}", entityId, json);
        }

        if (shown < total) {
            log.info("... ({} more not shown)", (total - shown));
        }
        log.info("===============================================");
        lock.unlock();
    }

    private static String componentsToJsonArray(Bag<Component> bag) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int j = 0, n = bag.size(); j < n; j++) {
            if (j > 0)
                sb.append(',');
            String name = bag.get(j).getClass().getSimpleName();
            sb.append('"').append(name).append('"');
        }
        sb.append(']');
        return sb.toString();
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

    public static EntityQueryService query() {
        if (queryService == null) {
            throw new IllegalStateException("EntityQueryService not initialized. Call GameWorld.initialize() first.");
        }
        return queryService;
    }

    private static class SingletonHolder {
        private static final GameWorld instance = new GameWorld();
    }

    public static GameWorld getInstance() {
        return SingletonHolder.instance;
    }

    public void submitToMain(Runnable task) {
        if (task != null)
            mainQueue.add(task);
    }

    public boolean isRunning() {
        return running;
    }
}