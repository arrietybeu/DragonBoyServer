package nro.repositories.player;

import lombok.Getter;
import nro.model.item.Item;
import nro.server.LogServer;
import nro.service.ItemService;

import java.sql.*;
import java.util.List;

@SuppressWarnings("ALL")
public class PlayerCreator {

    @Getter
    private static final PlayerCreator instance = new PlayerCreator();

    /**
     * Tạo một player mới với các thông tin cơ bản
     *
     * @param connection Connection kết nối tới cơ sở dữ liệu
     * @param accountId  ID của tài khoản
     * @param name       Tên của player
     * @param gender     Hành tinh (1 trái đất, 2 namec, 3 xayda)
     * @param hair       Kiểu tóc của player
     * @return true nếu tạo thành công, false nếu thất bại
     * @throws SQLException Lỗi liên quan tới cơ sở dữ liệu
     */

    public boolean createPlayer(Connection connection, int accountId, String name, byte gender, int hair) throws SQLException {
        var ms = System.currentTimeMillis();
        try {
            connection.setAutoCommit(false);
            int playerId = this.createPlayerBase(connection, accountId, name, gender, hair);
            if (playerId > 0) {
                this.createCurrenciesPlayer(connection, playerId);

                this.createLocationPlayer(connection, playerId, gender);

                this.createPlayerPoint(connection, playerId, gender);

                this.createMagicTreePlayer(connection, playerId);

                this.createItemBodyPlayer(connection, playerId, gender);

                connection.commit();
                LogServer.DebugLogic("Time create player name: " + name + " times: "
                        + (System.currentTimeMillis() - ms) + " ms");
                return true;
            } else {
                connection.rollback();
                return false;
            }
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
            LogServer.LogException(String.format("Error Create Player - AccountID: %d, Name: %s, gender: %d. Error: %s",
                    accountId, name, gender, e.getMessage()));
            return false;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private int createPlayerBase(Connection connection, int accountId, String name, byte gender, int head) throws SQLException {
        var ms = System.currentTimeMillis();
        int playerId;
        try (CallableStatement stmt = connection.prepareCall("{CALL `CreatePlayerBase`(?, ?, ?, ?, ?)}")) {
            stmt.setInt(1, accountId);
            stmt.setString(2, name);
            stmt.setByte(3, gender);
            stmt.setInt(4, head);
            stmt.registerOutParameter(5, java.sql.Types.INTEGER);
            stmt.execute();
            playerId = stmt.getInt(5);
        }
        if (playerId == 0) {
            throw new SQLException("Failed to create player.");
        }
        LogServer.DebugLogic("Time create player base: " + (System.currentTimeMillis() - ms) + " ms");

        return playerId;
    }

    private void createLocationPlayer(Connection connection, int playerId, byte gender) throws SQLException {
        String query = "INSERT INTO player_location (player_id, pos_x, pos_y, map_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, playerId);
            statement.setInt(2, 100); // pos_x
            statement.setInt(3, 384); // pos_y
            statement.setInt(4, 39 + gender); // map_id
            var row = statement.executeUpdate();
            if (row == 0) {
                LogServer.LogException("No rows were inserted into player_location for playerId: " + playerId);
                throw new SQLException("Failed to insert player location.");
            }
        }
    }

    private void createCurrenciesPlayer(Connection connection, int playerId) throws SQLException {
        String query = "INSERT INTO player_currencies (player_id, gold, gem, ruby) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, playerId);
            statement.setInt(2, 2000); // gold
            statement.setInt(3, 0); // gem
            statement.setInt(4, 50); // ruby
            if (statement.executeUpdate() == 0) {
                LogServer.LogException("No rows were inserted into player_currencies for playerId: " + playerId);
                throw new SQLException("Failed to insert player currencies.");
            }
        }
    }

    private void createPlayerPoint(Connection connection, int playerId, byte gender) throws SQLException {
        String query = "INSERT INTO player_point (player_id, hp, hp_default, mp, mp_default, dame_default, stamina, max_stamina, crit_default, def_default, tiem_nang, power, limit_power, nang_dong) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, playerId);
            statement.setInt(2, gender == 1 ? 200 : 100); // hp
            statement.setInt(3, gender == 1 ? 200 : 100); // hp_default
            statement.setInt(4, gender == 1 ? 200 : 100); // mp
            statement.setInt(5, gender == 1 ? 200 : 100); // mp_default
            statement.setInt(6, gender == 2 ? 15 : 10); // dame_default
            statement.setInt(7, 1000); // stamina
            statement.setInt(8, 1000); // max_stamina
            statement.setInt(9, 0); // crit_default
            statement.setInt(10, 0); // def_default
            statement.setInt(11, 2000); // tiem_nang
            statement.setInt(12, 2000); // power
            statement.setInt(13, 100); // limit_power
            statement.setInt(14, 0); // nang_dong
            if (statement.executeUpdate() == 0) {
                LogServer.LogException("No rows were inserted into player_point for playerId: " + playerId);
                throw new SQLException("Failed to insert player point.");
            }
        }
    }

    private void createMagicTreePlayer(Connection connection, int playerId) throws SQLException {
        String query = "INSERT INTO player_magic_tree (player_id, is_upgrade, time_upgrade, level, time_harvest, curr_pea) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, playerId);
            statement.setInt(2, 0); // is_upgrade
            statement.setLong(3, System.currentTimeMillis()); // time_upgrade
            statement.setInt(4, 1); // level
            statement.setLong(5, System.currentTimeMillis()); // time_harvest
            statement.setInt(6, 5); // curr_pea
            if (statement.executeUpdate() == 0) {
                LogServer.LogException("No rows were inserted into player_magic_trêe for playerId: " + playerId);
                throw new SQLException("Failed to insert player magic tree.");
            }
        }
    }

    private void createItemBodyPlayer(Connection connection, int playerId, byte gender) throws SQLException {
        List<Item> items = ItemService.initializePlayerItems(gender);
        if (items.isEmpty()) {
            throw new SQLException("Failed to initialize player items id: " + playerId);
        }
        String query = "INSERT INTO player_items_body (player_id, temp_id, quantity, options) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (Item item : items) {
                statement.setInt(1, playerId);
                statement.setInt(2, item.getTemplate().id());
                statement.setInt(3, item.getQuantity());
                statement.setString(4, item.getJsonOptions());
                statement.addBatch();
            }
            int rows = statement.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Failed to create item body player.");
            }
        }
    }

    private void createPlayerDataTask(Connection connection, int playerId) throws SQLException {
        String query = "INSERT INTO player_task (player_id, task_id, task_type, task_level, task_status, task_time, task_count) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, playerId);
            statement.setInt(2, 1); // task_id
            statement.setInt(3, 1); // task_type
            statement.setInt(4, 1); // task_level
            statement.setInt(5, 0); // task_status
            statement.setLong(6, System.currentTimeMillis()); // task_time
            statement.setInt(7, 0); // task_count
            if (statement.executeUpdate() == 0) {
                LogServer.LogException("No rows were inserted into player_task for playerId: " + playerId);
                throw new SQLException("Failed to insert player task.");
            }
        }
    }


}
