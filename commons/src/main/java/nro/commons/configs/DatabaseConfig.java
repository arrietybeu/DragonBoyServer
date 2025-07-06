package nro.commons.configs;


import nro.commons.configuration.Property;

public class DatabaseConfig {

    @Property(key = "database.user")
    public static String DATABASE_USER;

    @Property(key = "database.password")
    public static String DATABASE_PASSWORD;

    @Property(key = "database.connection-pool.connections.max", defaultValue = "5")
    public static int DATABASE_CONNECTIONS_MAX;

    @Property(key = "database.connection-pool.timeout", defaultValue = "5000")
    public static int DATABASE_TIMEOUT;

    @Property(key = "database.connection-pool.leak-detection.threshold" , defaultValue = "10000")
    public static int DATABASE_LEAK_DETECTION_THRESHOLD;

    @Property(key = "database.connection-pool.idle.timeout", defaultValue = "600000")
    public static int DATABASE_IDLE_TIMEOUT;

    @Property(key = "database.connection-pool.keepalive-timeout", defaultValue = "600000")
    public static int DATABASE_KEEPALIVE_TIMEOUT;

    @Property(key = "database.host")
    public static String DATABASE_HOST;

    @Property(key = "database.port")
    public static int DATABASE_PORT;

    @Property(key = "database.name")
    public static String DATABASE_NAME;

    @Property(key = "database.serverTimezone")
    public static String DATABASE_TIMEZONE;

    @Property(key = "database.characterEncoding")
    public static String DATABASE_ENCODING;

    public static String getDatabaseUrl() {
        return String.format(
                "jdbc:mysql://%s:%d/%s?serverTimezone=%s&characterEncoding=%s",
                DATABASE_HOST,
                DATABASE_PORT,
                DATABASE_NAME,
                DATABASE_TIMEZONE,
                DATABASE_ENCODING
        );
    }
}
