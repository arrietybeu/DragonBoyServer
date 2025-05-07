package nro.server.configs.main;

import nro.commons.configuration.Property;

public class ThreadConfig {

    @Property(key = "game-server.thread.base_pool_size", defaultValue = "0")
    public static int BASE_THREAD_POOL_SIZE;

    @Property(key = "game-server.thread.scheduled_pool_size", defaultValue = "0")
    public static int SCHEDULED_THREAD_POOL_SIZE;

    @Property(key = "game-server.thread.runtime", defaultValue = "5000")
    public static long MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING;

    @Property(key = "game-server.thread.use-priority", defaultValue = "false")
    public static boolean USE_PRIORITIES;

}
