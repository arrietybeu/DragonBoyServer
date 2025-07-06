package nro.commons.database;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface TransactionHandler {
    boolean handle(Connection con) throws SQLException;
}

