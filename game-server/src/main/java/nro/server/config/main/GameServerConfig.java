package nro.server.config.main;

import nro.commons.configuration.Property;

import java.time.ZoneId;

public class GameServerConfig {

    @Property(key = "gameserver.timezone")
    public static ZoneId TIME_ZONE_ID;

}
