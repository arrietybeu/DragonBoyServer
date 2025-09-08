package nro.commons.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import nro.commons.configs.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @Author Arriety
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final  class DatabaseFactory {

    private static HikariDataSource dataSource;

    private static final Logger log = LoggerFactory.getLogger(DatabaseFactory.class);

    public synchronized static void init() {
        if (dataSource != null) return;

        HikariConfig config = new HikariConfig();


        config.setJdbcUrl(DatabaseConfig.getDatabaseUrl());
        config.setPoolName("NRO-Game-Server-Pool");
        config.setUsername(DatabaseConfig.DATABASE_USER);
        config.setPassword(DatabaseConfig.DATABASE_PASSWORD);
        config.setMaximumPoolSize(DatabaseConfig.DATABASE_CONNECTIONS_MAX);
        config.setConnectionTimeout(DatabaseConfig.DATABASE_TIMEOUT);
        config.setLeakDetectionThreshold(DatabaseConfig.DATABASE_LEAK_DETECTION_THRESHOLD);

        config.setIdleTimeout(DatabaseConfig.DATABASE_IDLE_TIMEOUT);
        config.setMinimumIdle(10);
        config.setKeepaliveTime(DatabaseConfig.DATABASE_KEEPALIVE_TIMEOUT); // 10 phut
        config.setAutoCommit(true);

        dataSource = new HikariDataSource(config);

        log.info("HikariCP initialized with max pool size: {}", config.getMaximumPoolSize());
    }

    public static Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        if (!con.getAutoCommit()) {
            log.error("Connection was not in auto-commit mode.", new IllegalStateException());
            con.setAutoCommit(true);
        }
        return con;
    }

    public static String getStatsPool() {
        if (dataSource == null) {
            return "HikariCP chua duoc khoi tao.";
        }

        var poolBean = dataSource.getHikariPoolMXBean();
        var configMXBean = dataSource.getHikariConfigMXBean();

        return "=== Trang thai Connection Pool (HikariCP) ===\n" +

                // Thong tin cau hinh pool
                "Ten pool: " + configMXBean.getPoolName() + "\n" +
                "So luong toi da ket noi (maximumPoolSize): " + configMXBean.getMaximumPoolSize() + "\n" +
                "So ket noi nho nhat idle (minimumIdle): " + configMXBean.getMinimumIdle() + "\n" +
                "Thoi gian timeout ket noi (connectionTimeout ms): " + configMXBean.getConnectionTimeout() + "\n" +
                "Thoi gian keepalive (ms): " + dataSource.getKeepaliveTime() + "\n" +
                "Thoi gian phat hien leak (leakDetectionThreshold ms): " + configMXBean.getLeakDetectionThreshold() + "\n" +
                "\n" +

                // Thong tin runtime
                "So ket noi dang dang duoc su dung (active): " + poolBean.getActiveConnections() + "\n" +
                "So ket noi dang ranh (idle): " + poolBean.getIdleConnections() + "\n" +
                "Tong so ket noi trong pool (total): " + poolBean.getTotalConnections() + "\n" +
                "So ket noi dang cho (threads awaiting connection): " + poolBean.getThreadsAwaitingConnection() + "\n" +
                "============================================";
    }

}
