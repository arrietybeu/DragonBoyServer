package nro.server.engine;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lớp Singleton quản lý vòng đời và truy cập toàn cục tới ECS World.
 * Được cải tiến để linh hoạt hơn trong việc khởi tạo và quản lý hệ thống.
 *
 * @author Arriety
 */
public class GameWorld implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(GameWorld.class);

    private World world;
    private volatile boolean running = false;
    private Thread gameThread;

    private final long tickInterval;

    private GameWorld() {
        int ticksPerSecond = 60;
        this.tickInterval = 1000 / ticksPerSecond;
    }

    public void initialize(WorldConfigurationBuilder builder) {
        if (this.world != null) {
            log.warn("GameWorld has already been initialized.");
            return;
        }
        WorldConfiguration config = builder.build();
        this.world = new World(config);
        log.info("Artemis-odb World initialized successfully.");
    }

    public void start() {
        if (world == null) {
            log.error("World is not initialized. Cannot start GameLoop. Please call initialize() first.");
            return;
        }
        if (running) {
            log.warn("GameLoop is already running.");
            return;
        }

        running = true;
        gameThread = new Thread(this, "GameWorld-Thread");
        gameThread.start();
        log.info("GameLoop started with {} systems.", world.getSystems().size());
    }

    @Override
    public void run() {
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
                    Thread.sleep(tickInterval - elapsed);
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
        try {
            if (gameThread != null) {
                gameThread.join(5000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while waiting for game thread to shut down.", e);
        }
        if (world != null) {
            world.dispose();
        }
        log.info("GameWorld has been shut down.");
    }

    /**
     * Trả về đối tượng World của Artemis để các phần khác của game có thể tương tác.
     *
     * @return a World object from Artemis-odb
     */
    public World getWorld() {
        return this.world;
    }

    private static class SingletonHolder {
        private static final GameWorld instance = new GameWorld();
    }

    public static GameWorld getInstance() {
        return SingletonHolder.instance;
    }

}