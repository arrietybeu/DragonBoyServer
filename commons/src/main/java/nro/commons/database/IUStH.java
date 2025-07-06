package nro.commons.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface IUStH {
    void handleInsertUpdate(PreparedStatement stmt) throws SQLException;
}