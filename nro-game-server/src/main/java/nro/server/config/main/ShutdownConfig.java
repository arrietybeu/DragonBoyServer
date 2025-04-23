package nro.server.config.main;

import nro.commons.configuration.Property;
import org.quartz.CronExpression;

public class ShutdownConfig {

    /**
     * Shutdown Hook delay in seconds.
     */
    @Property(key = "gameserver.shutdown.delay", defaultValue = "120")
    public static int DELAY;

    /**
     * Shutdown restart schedule.
     */
    @Property(key = "gameserver.shutdown.restart_schedule")
    public static CronExpression RESTART_SCHEDULE;

}
