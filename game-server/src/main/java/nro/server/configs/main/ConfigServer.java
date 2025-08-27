package nro.server.configs.main;

import nro.commons.configuration.Property;

import java.time.ZoneId;

public class ConfigServer {

    //    @Property(key = "game-server.thread.base_pool_size", defaultValue = "0")
    public static boolean IS_OPEN_UI_LOG_BUG = true;

    @Property(key = "game-server.version.data", defaultValue = "1")
    public static int VERSION_DATA;

    @Property(key = "game-server.version.data-map", defaultValue = "1")
    public static int VERSION_DATA_MAP;

    @Property(key = "game-server.version.data-skill", defaultValue = "1")
    public static int VERSION_DATA_SKILL;

    @Property(key = "game-server.version.data-item", defaultValue = "1")
    public static byte VERSION_DATA_ITEM;

    @Property(key = "game-server.timezone")
    public static ZoneId TIME_ZONE_ID;

    @Property(key = "game-server.is_debug", defaultValue = "false")
    public static boolean IS_DEBUG;

}
