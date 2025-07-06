package nro.commons.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Dùng khi cần truyền tham số vào PreparedStatement để thực hiện truy vấn.
 * @author Arriety
 */

public interface ParamReadStatementHandler extends ReadStatementHandler {
    void setParams(PreparedStatement stmt) throws SQLException;
}
