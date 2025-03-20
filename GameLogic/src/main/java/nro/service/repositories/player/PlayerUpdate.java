package nro.service.repositories.player;

import lombok.Getter;
import nro.service.model.model.item.Item;
import nro.service.model.model.player.Player;
import nro.service.model.model.player.PlayerMagicTree;
import nro.service.model.model.player.PlayerPoints;
import nro.service.model.model.task.TaskMain;
import nro.service.model.model.template.entity.SessionInfo;
import nro.service.repositories.DatabaseConnectionPool;
import nro.server.LogServer;
import nro.server.config.ConfigDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("ALL")
public class PlayerUpdate {

    @Getter
    private static final PlayerUpdate instance = new PlayerUpdate();

    public void savePlayer(Player player) {
        SessionInfo sessionInfo = player.getSession().getSessionInfo();
        if (sessionInfo.isSaveData()) {
            LogServer.LogWarning("Đang save Data sao lại save lại??");
            return;
        }
        sessionInfo.setSaveData(true);
        var ms = System.currentTimeMillis();

        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_DYNAMIC)) {
            if (connection == null) {
                throw new SQLException("Lỗi: Không thể kết nối database để lưu dữ liệu.");
            }
            connection.setAutoCommit(false);
            try {
                this.savePlayerInfo(player, connection);
                this.savePlayerCurrencies(player, connection);
                this.savePlayerStats(player, connection);
                this.savePlayerLocation(player, connection);
                this.savePlayerSkillsShortCut(player, connection);
                this.savePlayerTask(player, connection);
                this.savePlayerMagicTree(player, connection);
                this.savePlayerInventory(player, connection);

                connection.commit();

                long time = System.currentTimeMillis() - ms;
                LogServer.DebugLogic("Last time Save Data Player: " + player.getName() + " " + time + " ms");

            } catch (SQLException ex) {
                LogServer.LogException("Lỗi khi lưu dữ liệu Player: " + ex.getMessage());
                try {
                    connection.rollback();
                    LogServer.LogException("Rollback Transaction: Dữ liệu chưa được lưu.");
                } catch (SQLException rollbackEx) {
                    LogServer.LogException("Lỗi khi rollback: " + rollbackEx.getMessage());
                }
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException ex) {
                    LogServer.LogException("Lỗi khi reset AutoCommit: " + ex.getMessage());
                }
            }

        } catch (SQLException ex) {
            LogServer.LogException("savePlayer: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            sessionInfo.setSaveData(false);
        }
    }

    private void savePlayerInfo(Player player, Connection connection) throws SQLException {
        String query = "UPDATE player SET name = ?, gender = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, player.getName());
            statement.setByte(2, player.getGender());
            statement.setInt(3, player.getId());
            statement.executeUpdate();
        }
    }

    private void savePlayerInventory(Player player, Connection connection) throws SQLException {
        var inventory = player.getPlayerInventory();
        updateInventorySize(connection, player.getId(), "max_bag_size", inventory.getItemBagSize(), inventory.getItemsBag().size(), size -> inventory.setItemBagSize(size), "player_items_bag");
        updateInventorySize(connection, player.getId(), "max_box_size", inventory.getItemBoxSize(), inventory.getItemsBox().size(), size -> inventory.setItemBoxSize(size), "player_items_box");
        saveInventoryItems(player, connection, "player_items_body", inventory.getItemsBody());
        saveInventoryItems(player, connection, "player_items_bag", inventory.getItemsBag());
        saveInventoryItems(player, connection, "player_items_box", inventory.getItemsBox());
    }

    private void updateInventorySize(Connection connection, int playerId, String column, int oldSize, int newSize, Consumer<Integer> updateSize, String tableName) throws SQLException {
        if (oldSize < newSize) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE player SET " + column + " = ? WHERE id = ?")) {
                statement.setInt(1, newSize);
                statement.setInt(2, playerId);
                statement.executeUpdate();
            }
            updateSize.accept(newSize);
            insertIndexInventory(connection, playerId, tableName, oldSize, newSize);
        }
    }

    private void insertIndexInventory(Connection connection, int playerId, String tableName, int oldSize, int newSize) throws SQLException {
        oldSize = Math.max(oldSize, getMaxRowIndex(connection, playerId, tableName) + 1);

        if (oldSize >= newSize) return;

        String query = "INSERT INTO " + tableName + " (player_id, row_index, temp_id, quantity, options) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE temp_id = VALUES(temp_id), quantity = VALUES(quantity), options = VALUES(options)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            boolean hasBatch = false;

            for (int index = oldSize; index < newSize; index++) {
                statement.setInt(1, playerId);
                statement.setInt(2, index);
                statement.setInt(3, -1);
                statement.setInt(4, 0);
                statement.setString(5, "[]");
                statement.addBatch();
                hasBatch = true;
            }

            if (hasBatch) {
                int[] rowsAffected = statement.executeBatch();
                if (Arrays.stream(rowsAffected).sum() == 0) {
                    throw new SQLException("Failed to insert items into " + tableName);
                }
            }
        } catch (SQLException ex) {
            LogServer.LogException("Lỗi khi insert index inventory: " + ex.getMessage(), ex);
        }
    }

    private int getMaxRowIndex(Connection connection, int playerId, String tableName) throws SQLException {
        String query = "SELECT COALESCE(MAX(row_index), -1) FROM " + tableName + " WHERE player_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, playerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        return -1;
    }

    private void saveInventoryItems(Player player, Connection connection, String tableName, List<Item> inventory) throws SQLException {
        if (inventory.isEmpty()) return;

        StringBuilder query = new StringBuilder("UPDATE " + tableName + " SET temp_id = CASE row_index ");

        for (int index = 0; index < inventory.size(); index++) {
            query.append("WHEN ").append(index).append(" THEN ? ");
        }
        query.append(" END, quantity = CASE row_index ");

        for (int index = 0; index < inventory.size(); index++) {
            query.append("WHEN ").append(index).append(" THEN ? ");
        }
        query.append(" END, options = CASE row_index ");

        for (int index = 0; index < inventory.size(); index++) {
            query.append("WHEN ").append(index).append(" THEN ? ");
        }
        query.append(" END WHERE player_id = ? AND row_index IN (");

        for (int index = 0; index < inventory.size(); index++) {
            query.append(index).append(index < inventory.size() - 1 ? ", " : ")");
        }

        try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
            int paramIndex = 1;
            for (Item item : inventory) {
                statement.setInt(paramIndex++, item.getTemplate() == null ? -1 : item.getTemplate().id());
            }
            for (Item item : inventory) {
                statement.setInt(paramIndex++, item.getQuantity());
            }
            for (Item item : inventory) {
                statement.setString(paramIndex++, item.getJsonOptions());
            }
            statement.setInt(paramIndex++, player.getId());

            statement.executeUpdate();
        }
    }


    private void savePlayerCurrencies(Player player, Connection connection) throws SQLException {
        String query = "UPDATE player_currencies SET gold = ?, gem = ?, ruby = ? WHERE player_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, player.getPlayerCurrencies().getGold());
            statement.setInt(2, player.getPlayerCurrencies().getGem());
            statement.setInt(3, player.getPlayerCurrencies().getRuby());
            statement.setInt(4, player.getId());
            statement.executeUpdate();
        }
    }

    private void savePlayerStats(Player player, Connection connection) throws SQLException {
        String query = "UPDATE player_point SET hp = ?, hp_max = ?, hp_current = ?, " + "mp = ?, mp_max = ?, mp_current = ?, " + "dame_max = ?, dame_default = ?, " + "crit = ?, crit_default = ?, " + "defense = ?, def_default = ?, " + "stamina = ?, max_stamina = ?, " + "power = ?, limit_power = ?, " + "tiem_nang = ?, nang_dong = ? " + "WHERE player_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            PlayerPoints stats = player.getPlayerPoints();
            statement.setLong(1, stats.getBaseHP());
            statement.setLong(2, stats.getMaxHP());
            statement.setLong(3, stats.getCurrentHP());
            statement.setLong(4, stats.getBaseMP());
            statement.setLong(5, stats.getMaxMP());
            statement.setLong(6, stats.getCurrentMP());
            statement.setLong(7, stats.getCurrentDamage());
            statement.setLong(8, stats.getBaseDamage());
            statement.setByte(9, stats.getBaseCriticalChance());
            statement.setInt(10, stats.getTotalCriticalChance());
            statement.setInt(11, stats.getBaseDefense());
            statement.setLong(12, stats.getTotalDefense());
            statement.setShort(13, stats.getStamina());
            statement.setShort(14, stats.getMaxStamina());
            statement.setLong(15, stats.getPower());
            statement.setInt(16, stats.getExpPerStatIncrease());
            statement.setLong(17, stats.getPotentialPoints());
            statement.setLong(18, player.getActivePoint());
            statement.setInt(19, player.getId());
            statement.executeUpdate();
        }
    }

    private void savePlayerLocation(Player player, Connection connection) throws SQLException {
        String query = "UPDATE player_location SET pos_x = ?, pos_y = ?, map_id = ? WHERE player_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            var x = player.getX();
            var y = player.getY();
            short mapID = (short) player.getArea().getMap().getId();
            if (x < 0 || y < 0) {
                x = 200;
                y = 336;
                mapID = (short) (21 + player.getGender());
            }
            statement.setShort(1, x);
            statement.setShort(2, y);
            statement.setShort(3, mapID);
            statement.setInt(4, player.getId());
            statement.executeUpdate();
        }
    }

    private void savePlayerSkillsShortCut(Player player, Connection connection) throws SQLException {
        String query = "UPDATE player_skills_shortcut SET slot_1 = ?, slot_2 = ?, slot_3 = ?, " + "slot_4 = ?, slot_5 = ?, slot_6 = ?, slot_7 = ?, slot_8 = ?, slot_9 = ?, slot_10 = ? " + "WHERE player_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            byte[] skillShortCut = player.getPlayerSkill().getSkillShortCut();
            for (int i = 0; i < 10; i++) {
                statement.setByte(i + 1, skillShortCut[i]);
            }
            statement.setInt(11, player.getId());
            statement.executeUpdate();
        }
    }

    private void savePlayerTask(Player player, Connection connection) throws SQLException {
        String query = "UPDATE player_task SET task_id = ?, task_index = ?, task_count = ? WHERE player_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            TaskMain task = player.getPlayerTask().getTaskMain();
            statement.setInt(1, task.getId());
            statement.setInt(2, task.getIndex());
            statement.setShort(3, task.getSubNameList().get(task.getIndex()).getCount());
            statement.setInt(4, player.getId());
            statement.executeUpdate();
        }
    }

    private void savePlayerMagicTree(Player player, Connection connection) throws SQLException {
        String query = "UPDATE player_magic_tree SET is_upgrade = ?, level = ?, curr_pea = ?, time_upgrade = ?, time_harvest = ? WHERE player_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            PlayerMagicTree magicTree = player.getPlayerMagicTree();
            statement.setInt(1, magicTree.isUpgrade() ? 1 : 0);
            statement.setInt(2, magicTree.getLevel());
            statement.setInt(3, magicTree.getCurrPeas());
            statement.setLong(4, magicTree.getLastTimeUpgrade());
            statement.setLong(5, magicTree.getLastTimeHarvest());
            statement.setInt(6, player.getId());
            statement.executeUpdate();
        }
    }

}
