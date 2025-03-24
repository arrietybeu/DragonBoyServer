/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.service.repositories;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

import nro.server.config.ConfigDB;
import nro.server.system.LogServer;

/**
 * @author Arriety
 */
public class DatabaseConnectionPool {

    private static final HikariDataSource DYNAMIC_DATA_SOURCE;
    private static final HikariDataSource STATIC_DATA_SOURCE;
    private static final HikariDataSource ENTITY_DATA_SOURCE;

    static {
        /*
         * Config Database Dynamic
         */
        HikariConfig dynamicConfig = new HikariConfig();
        dynamicConfig.setDriverClassName(ConfigDB.DRIVER);
        dynamicConfig.setJdbcUrl(ConfigDB.DB_DYNAMIC_URL);
        dynamicConfig.setUsername(ConfigDB.DB_DYNAMIC_USER);
        dynamicConfig.setPassword(ConfigDB.DB_DYNAMIC_PASSWORD);

        dynamicConfig.addDataSourceProperty("cachePrepStmts", "true");
        dynamicConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        dynamicConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dynamicConfig.addDataSourceProperty("characterEncoding", "utf8");
        dynamicConfig.addDataSourceProperty("useUnicode", "true");

        dynamicConfig.setMinimumIdle(20);
        dynamicConfig.setMaximumPoolSize(250); // edit size kết nối tối đa
        dynamicConfig.setConnectionTimeout(30000); // time chờ kết nối
        dynamicConfig.setIdleTimeout(60000); // close kết nối sau 60s no sử dụng
        dynamicConfig.setConnectionTestQuery("SELECT 1"); // check kết nối
        dynamicConfig.setLeakDetectionThreshold(3000); // phát hiện rò rỉ sau 2 giây

        DYNAMIC_DATA_SOURCE = new HikariDataSource(dynamicConfig);

        /*
         * Config Database Static
         */
        HikariConfig staticConfig = new HikariConfig();
        staticConfig.setDriverClassName(ConfigDB.DRIVER);
        staticConfig.setJdbcUrl(ConfigDB.DB_STATIC_URL);
        staticConfig.setUsername(ConfigDB.DB_STATIC_USER);
        staticConfig.setPassword(ConfigDB.DB_STATIC_PASSWORD);

        staticConfig.addDataSourceProperty("cachePrepStmts", "true");
        staticConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        staticConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        staticConfig.addDataSourceProperty("characterEncoding", "utf8");
        staticConfig.addDataSourceProperty("useUnicode", "true");

        staticConfig.setMinimumIdle(5);
        staticConfig.setMaximumPoolSize(50); // gig hạn số kết nối cho static database
        staticConfig.setConnectionTimeout(30000); // time connect
        staticConfig.setIdleTimeout(60000); // close kết nối sau 60s no sử dụng
        staticConfig.setConnectionTestQuery("SELECT 1"); // check connect
//        staticConfig.setLeakDetectionThreshold(3000);// canh bao ro ri ket noi 3s

        STATIC_DATA_SOURCE = new HikariDataSource(staticConfig);

        /*
         * Config Database Entity (Boss, NPC, Bot)
         */
        HikariConfig entityConfig = new HikariConfig();
        entityConfig.setDriverClassName(ConfigDB.DRIVER);
        entityConfig.setJdbcUrl(ConfigDB.DB_ENTITY_URL);
        entityConfig.setUsername(ConfigDB.DB_ENTITY_USER);
        entityConfig.setPassword(ConfigDB.DB_ENTITY_PASSWORD);

        entityConfig.addDataSourceProperty("cachePrepStmts", "true");
        entityConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        entityConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        entityConfig.addDataSourceProperty("characterEncoding", "utf8");
        entityConfig.addDataSourceProperty("useUnicode", "true");

        entityConfig.setMinimumIdle(5);
        entityConfig.setMaximumPoolSize(50); // Giới hạn số kết nối cho database entity
        entityConfig.setConnectionTimeout(30000); // Thời gian chờ kết nối
        entityConfig.setIdleTimeout(60000); // Đóng kết nối sau 60s không sử dụng
        entityConfig.setConnectionTestQuery("SELECT 1"); // Kiểm tra kết nối

        ENTITY_DATA_SOURCE = new HikariDataSource(entityConfig);
    }

    public static Connection getConnectionForTask(int taskIndex, String... method) {
        try {
            return switch (taskIndex) {
                case ConfigDB.DATABASE_DYNAMIC -> DYNAMIC_DATA_SOURCE.getConnection();
                case ConfigDB.DATABASE_STATIC -> STATIC_DATA_SOURCE.getConnection();
                case ConfigDB.DATABASE_ENTITY -> ENTITY_DATA_SOURCE.getConnection();
                default -> {
                    LogServer.LogException("Invalid taskIndex: " + taskIndex);
                    yield null;
                }
            };
        } catch (SQLException e) {
            LogServer.LogException("Error in " + (method.length > 0 ? method[0] : "unknown") + ": " + e.getMessage());
            return null;
        }
    }

    // close all connect database
    public static void closeConnections() {
        try {
            if (STATIC_DATA_SOURCE != null && !STATIC_DATA_SOURCE.isClosed()) {
                STATIC_DATA_SOURCE.close();
            }
        } catch (Exception e) {
            LogServer.LogException("Error closing STATIC_DATA_SOURCE: " + e.getMessage());
        }

        try {
            if (DYNAMIC_DATA_SOURCE != null && !DYNAMIC_DATA_SOURCE.isClosed()) {
                DYNAMIC_DATA_SOURCE.close();
            }
        } catch (Exception e) {
            LogServer.LogException("Error closing DYNAMIC_DATA_SOURCE: " + e.getMessage());
        }

        try {
            if (ENTITY_DATA_SOURCE != null && !ENTITY_DATA_SOURCE.isClosed()) {
                ENTITY_DATA_SOURCE.close();
            }
        } catch (Exception e) {
            LogServer.LogException("Error closing ENTITY_DATA_SOURCE: " + e.getMessage());
        }
    }
}
