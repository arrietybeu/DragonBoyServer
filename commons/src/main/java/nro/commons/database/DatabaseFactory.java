package nro.commons.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import nro.commons.configs.DatabaseConfig;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseFactory {

    private static HikariDataSource DYNAMIC_DATA_SOURCE;
    private static HikariDataSource STATIC_DATA_SOURCE;
    private static HikariDataSource ENTITY_DATA_SOURCE;

    private DatabaseFactory() {
        System.out.println("cai lon?");
    }

    public synchronized static void init() {
        if (DYNAMIC_DATA_SOURCE != null) return;

        DYNAMIC_DATA_SOURCE = createDataSource(
                DatabaseConfig.DB_DYNAMIC_URL,
                DatabaseConfig.DB_DYNAMIC_USER,
                DatabaseConfig.DB_DYNAMIC_PASSWORD,
                20, 250
        );

        STATIC_DATA_SOURCE = createDataSource(
                DatabaseConfig.DB_STATIC_URL,
                DatabaseConfig.DB_STATIC_USER,
                DatabaseConfig.DB_STATIC_PASSWORD,
                5, 50
        );

        ENTITY_DATA_SOURCE = createDataSource(
                DatabaseConfig.DB_ENTITY_URL,
                DatabaseConfig.DB_ENTITY_USER,
                DatabaseConfig.DB_ENTITY_PASSWORD,
                5, 50
        );
    }

    private static HikariDataSource createDataSource(String url, String user, String pass,
                                                     int minIdle, int maxPool) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(DatabaseConfig.DRIVER);
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(pass);

        config.setMinimumIdle(minIdle);
        config.setMaximumPoolSize(maxPool);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(60000);
        config.setConnectionTestQuery("SELECT 1");
        config.setLeakDetectionThreshold(3000);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("characterEncoding", "utf8");
        config.addDataSourceProperty("useUnicode", "true");

        return new HikariDataSource(config);
    }

    public static Connection getConnection(DatabaseType type) throws SQLException {
        return switch (type) {
            case DYNAMIC -> getConnection(DYNAMIC_DATA_SOURCE);
            case STATIC -> getConnection(STATIC_DATA_SOURCE);
            case ENTITY -> getConnection(ENTITY_DATA_SOURCE);
        };
    }

    private static Connection getConnection(HikariDataSource source) throws SQLException {
        Connection con = source.getConnection();
        if (!con.getAutoCommit()) {
            LoggerFactory.getLogger(DatabaseFactory.class).error("Connection was not in auto-commit mode.", new IllegalStateException());
            con.setAutoCommit(true);
        }
        return con;
    }

    public static Connection getConnectionForTask(int taskIndex, String... method) {
        try {
            return switch (taskIndex) {
                case DatabaseConfig.DATABASE_DYNAMIC -> getConnection(DYNAMIC_DATA_SOURCE);
                case DatabaseConfig.DATABASE_STATIC -> getConnection(STATIC_DATA_SOURCE);
                case DatabaseConfig.DATABASE_ENTITY -> getConnection(ENTITY_DATA_SOURCE);
                default -> null;
            };
        } catch (SQLException e) {
            return null;
        }
    }

    public static void closeAll() {
        if (DYNAMIC_DATA_SOURCE != null) DYNAMIC_DATA_SOURCE.close();
        if (STATIC_DATA_SOURCE != null) STATIC_DATA_SOURCE.close();
        if (ENTITY_DATA_SOURCE != null) ENTITY_DATA_SOURCE.close();
    }
}
