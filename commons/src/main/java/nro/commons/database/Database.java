package nro.commons.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public final class Database {

    private static final Logger log = LoggerFactory.getLogger(Database.class);

    private Database() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Thực hiện truy vấn SELECT (chỉ đọc dữ liệu).
     * <p>
     * Dùng để lấy danh sách hoặc chi tiết dữ liệu từ DB. Ví dụ trong game:
     * <ul>
     *     <li>Truy vấn danh sách item trong kho đồ của player</li>
     *     <li>Truy vấn thông tin nhân vật khi đăng nhập</li>
     *     <li>Truy vấn bảng xếp hạng người chơi</li>
     * </ul>
     *
     * <p><b>Ví dụ sử dụng bằng Anonymous Class:</b></p>
     *
     * <pre><code>
     * Database.select(query, new ParamReadStatementHandler() {
     *     {@literal @}Override
     *     public void setParams(PreparedStatement stmt) throws SQLException {
     *         stmt.setInt(1, ...);
     *     }
     *
     *     {@literal @}Override
     *     public void handleRead(ResultSet rs) throws SQLException {
     *         if (rs.next()) {....
     *     }
     * });
     * </code></pre>
     *
     * <p><b>Ví dụ sử dụng bằng Lambda Expression:</b></p>
     * <p><i>(Yêu cầu bạn đã viết overload Database.select(...) với SQLConsumer)</i></p>
     *
     * <pre><code>
     * String query = "SELECT * FROM item WHERE player_id = ?";
     * Database.select(query,
     *     rs -> {
     *         if (rs.next()) {....
     *     }
     * );
     * </code></pre>
     *
     * @param query  Câu truy vấn SELECT
     * @param reader Hàm xử lý kết quả trả về (ResultSet)
     * @return true nếu truy vấn thành công, false nếu có lỗi
     */

    public static boolean select(String query, ReadStatementHandler reader) {
        try (Connection con = DatabaseFactory.getConnection();

             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet set = stmt.executeQuery()) {

            if (reader instanceof ParamReadStatementHandler paramReader)
                paramReader.setParams(stmt);

            reader.handleRead(set);

            return true;

        } catch (Exception e) {
            log.error("Error executing select query {}", query, e);
            return false;
        }
    }

    /**
     * Truy vấn SELECT với tham số, dùng để lấy dữ liệu cụ thể hơn
     * <pre><code>
     * Database.select(query, resultSet -> {
     *     // xử lý ResultSet
     * }, preparedStatement -> {
     *     // set tham số
     * });
     * </code></pre>
     * @param query
     * @param reader
     * @param paramSetter
     * @return
     */
    public static boolean select(String query, SQLConsumer<ResultSet> reader, SQLConsumer<PreparedStatement> paramSetter) {
        try (Connection con = DatabaseFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            if (paramSetter != null)
                paramSetter.accept(stmt);

            try (ResultSet rs = stmt.executeQuery()) {
                reader.accept(rs);
            }

            return true;
        } catch (Exception e) {
            log.error("Error executing select query {}", query, e);
            return false;
        }
    }


    /**
     * Cập nhật dữ liệu không có tham số (INSERT hoặc UPDATE)
     * <p>
     * Dùng để thêm mới hoặc cập nhật dữ liệu đơn giản
     * <p>
     * Ví dụ trong game:
     * <p>
     *   - Thêm người chơi mới vào database
     * <p>
     *   - Cập nhật vị trí hiện tại của nhân vật
     * @param query Câu truy vấn UPDATE
     */
    public static boolean insertUpdate(String query) {
        return insertUpdate(query, null);
    }

    public static boolean insertUpdate(String query, IUStH batch) {
        try (Connection con = DatabaseFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            if (batch != null)
                batch.handleInsertUpdate(stmt);
            else
                stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            log.error("Failed to execute IU query {}", query, e);
            return false;
        }
    }

    public static int executeUpdate(String sql) {
        try (Connection con = DatabaseFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            return stmt.executeUpdate();

        } catch (Exception e) {
            log.error("Execute update failed for SQL: {}", sql, e);
            return -1;
        }
    }

    private static void executeUpdate(PreparedStatement statement) {
        try {
            statement.executeUpdate();
        } catch (Exception e) {
            log.error("Can't execute update for PreparedStatement", e);
        }
    }

    public static void executeUpdateAndClose(PreparedStatement statement) {
        executeUpdate(statement);
        close(statement);
    }

    /**
     * Thực hiện chuỗi thao tác SQL trong 1 transaction.
     * <p>
     * Dùng khi muốn đảm bảo toàn bộ thao tác SQL thành công thì mới commit, nếu có lỗi thì rollback toàn bộ.
     * <p>
     * Ví dụ áp dụng trong game:
     * <ul>
     *     <li>Lưu toàn bộ thông tin player khi logout (inventory, stats, nhiệm vụ...)</li>
     *     <li>Trao thưởng nhiều phần quà sau event (thêm item + ghi log + update điểm)</li>
     * </ul>
     *
     * <p><b>Ví dụ sử dụng với Anonymous Class:</b></p>
     *
     * <pre><code>
     * Database.executeTransaction(new TransactionHandler() {
     *     {@literal @}Override
     *     public boolean handle(Connection con) throws SQLException {
     *         try (PreparedStatement insertItem = con.prepareStatement(
     *                 "INSERT INTO item (player_id, item_id) VALUES (?, ?)");
     *              PreparedStatement log = con.prepareStatement(
     *                 "INSERT INTO log (player_id, action) VALUES (?, ?)")) {
     *
     *             insertItem.setInt(1, playerId);
     *             insertItem.setInt(2, itemId);
     *             insertItem.executeUpdate();
     *
     *             log.setInt(1, playerId);
     *             log.setString(2, "Nhận thưởng");
     *             log.executeUpdate();
     *
     *             return true; // commit
     *         } catch (SQLException e) {
     *             // Tự động rollback vì throw lỗi
     *             throw e;
     *         }
     *     }
     * });
     * </code></pre>
     *
     * <p><b>Ví dụ sử dụng với Lambda (nếu TransactionHandler là Functional Interface):</b></p>
     *
     * <pre><code>
     * Database.executeTransaction(con -> {
     *     try (PreparedStatement stmt = con.prepareStatement(
     *             "UPDATE player SET gold = gold + ? WHERE id = ?")) {
     *
     *         stmt.setInt(1, 5000);
     *         stmt.setInt(2, playerId);
     *         stmt.executeUpdate();
     *         return true; // commit
     *     }
     * });
     * </code></pre>
     *
     * @param handler Hàm xử lý transaction, thao tác trên cùng một Connection
     * @return true nếu commit thành công, false nếu rollback (hoặc có lỗi)
     */

    public static boolean executeTransaction(TransactionHandler handler) {
        try (Connection con = DatabaseFactory.getConnection()) {
            con.setAutoCommit(false);

            try {
                boolean success = handler.handle(con);
                if (success) {
                    con.commit();
                } else {
                    con.rollback();
                }
                return success;
            } catch (Exception e) {
                try {
                    con.rollback();
                } catch (SQLException rollbackEx) {
                    log.error("Rollback failed", rollbackEx);
                }
                throw e;
            }

        } catch (Exception e) {
            log.error("Transaction failed", e);
            return false;
        }
    }


    /**
     * Đóng PreparedStatemet, kết nối của nó và ResultSet cuối cùng
     *
     * @param statement statement to close
     */
    public static void close(PreparedStatement statement) {
        if (statement == null) // e.g. null from failed DB.prepareStatement call
            return;
        try {
            if (statement.isClosed()) {
                // noinspection ThrowableInstanceNeverThrown
                log.warn("Attempt to close PreparedStatement that is closed already", new Exception());
                return;
            }

            Connection c = statement.getConnection();
            statement.close();
            c.close();
        } catch (Exception e) {
            log.error("Error while closing PreparedStatement", e);
        }
    }
}
