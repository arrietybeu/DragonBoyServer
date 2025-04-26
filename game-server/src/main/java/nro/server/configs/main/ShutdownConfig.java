package nro.server.configs.main;

import nro.commons.configuration.Property;
import org.quartz.CronExpression;

public class ShutdownConfig {

    /**
     * Shutdown Hook delay in seconds.
     */
    @Property(key = "game-server.shutdown.delay", defaultValue = "120")
    public static int DELAY;

    /**
     * Shutdown restart schedule.
     */
    @Property(key = "game-server.shutdown.restart_schedule")
    public static CronExpression RESTART_SCHEDULE;

}
