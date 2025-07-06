package nro.commons.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Arriety
 */
@FunctionalInterface
public interface ReadStatementHandler {
    void handleRead(ResultSet set) throws SQLException;
}

