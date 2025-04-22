package nro.server.config.main;

import nro.commons.configuration.Property;

@SuppressWarnings("ALL")
public class ThreadConfig {

    @Property(key = "gameserver.thread.base_pool_size", defaultValue = "0")
    public static int BASE_THREAD_POOL_SIZE;

    @Property(key = "gameserver.thread.scheduled_pool_size", defaultValue = "0")
    public static int SCHEDULED_THREAD_POOL_SIZE;

    @Property(key = "gameserver.thread.runtime", defaultValue = "5000")
    public static long MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING;

    @Property(key = "gameserver.thread.usepriority", defaultValue = "false")
    public static boolean USE_PRIORITIES;
}
