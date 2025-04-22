package nro.commons.configs;

import nro.commons.configuration.Property;

public class DatabaseConfig {

    public static final int DATABASE_DYNAMIC = 0;

    public static final int DATABASE_STATIC = 1;

    public static final int DATABASE_ENTITY = 2;

    @Property(key = "database.driver")
    public static String DRIVER;

    @Property(key = "database.url")
    public static String DATABASE_URL;

    @Property(key = "database.user")
    public static String DATABASE_USER;

    @Property(key = "database.password")
    public static String DATABASE_PASSWORD;

    // Database dynamic
    public static final String DB_DYNAMIC_HOST = "localhost";
    public static final int DB_PORT = 3306;
    public static final String DB_DYNAMIC_NAME = "nro_dynamic";
    public static final String DB_DYNAMIC_USER = "root";
    public static final String DB_DYNAMIC_PASSWORD = "";
    public static final String DB_DYNAMIC_URL = "jdbc:mysql://" + DB_DYNAMIC_HOST + ":" + DB_PORT + "/" + DB_DYNAMIC_NAME;

    // Database static
    public static final String DB_STATIC_HOST = "localhost";
    public static final int DB_STATIC_PORT = 3306;
    public static final String DB_STATIC_NAME = "nro_static";
    public static final String DB_STATIC_USER = "root";
    public static final String DB_STATIC_PASSWORD = "";
    public static final String DB_STATIC_URL = "jdbc:mysql://" + DB_STATIC_HOST + ":" + DB_STATIC_PORT + "/" + DB_STATIC_NAME;

    // Database entity
    public static final String DB_ENTITY_HOST = "localhost";
    public static final int DB_ENTITY_PORT = 3306;
    public static final String DB_ENTITY_NAME = "nro_entity";
    public static final String DB_ENTITY_USER = "root";
    public static final String DB_ENTITY_PASSWORD = "";
    public static final String DB_ENTITY_URL = "jdbc:mysql://" + DB_ENTITY_HOST + ":" + DB_ENTITY_PORT + "/" + DB_ENTITY_NAME;

    /**
     * Maximum amount of connections kept in connection pool
     */
    @Property(key = "database.connectionpool.connections.max", defaultValue = "5")
    public static int DATABASE_CONNECTIONS_MAX;
    /**
     * Maximum wait time when getting a DB connection, before throwing a timeout error
     */
    @Property(key = "database.connectionpool.timeout", defaultValue = "5000")
    public static int DATABASE_TIMEOUT;


}
