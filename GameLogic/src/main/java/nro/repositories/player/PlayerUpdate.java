package nro.repositories.player;

import lombok.Getter;
import nro.model.player.Player;
import nro.model.player.PlayerStats;
import nro.model.task.TaskMain;
import nro.model.template.entity.SessionInfo;
import nro.repositories.DatabaseConnectionPool;
import nro.server.LogServer;
import nro.server.config.ConfigDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
        String query = "UPDATE player SET name = ?, gender = ?, head = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, player.getName());
            statement.setByte(2, player.getGender());
            statement.setShort(3, player.getPlayerFashion().getHead());
            statement.setInt(4, player.getId());
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
        String query = "UPDATE player_point SET hp = ?, hp_max = ?, hp_current = ?, " +
                "mp = ?, mp_max = ?, mp_current = ?, " +
                "dame_max = ?, dame_default = ?, " +
                "crit = ?, crit_default = ?, " +
                "defense = ?, def_default = ?, " +
                "stamina = ?, max_stamina = ?, " +
                "power = ?, limit_power = ?, " +
                "tiem_nang = ?, nang_dong = ? " +
                "WHERE player_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            PlayerStats stats = player.getPlayerStats();
            statement.setLong(1, stats.getBaseHP());
            statement.setLong(2, stats.getMaxHP());
            statement.setLong(3, stats.getCurrentHP());
            statement.setLong(4, stats.getBaseMP());
            statement.setLong(5, stats.getMaxMP());
            statement.setLong(6, stats.getCurrentMP());
            statement.setLong(7, stats.getTotalDamage());
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
            if (x < 20 || y < 20) {
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
        String query = "UPDATE player_skills_shortcut SET slot_1 = ?, slot_2 = ?, slot_3 = ?, " +
                "slot_4 = ?, slot_5 = ?, slot_6 = ?, slot_7 = ?, slot_8 = ?, slot_9 = ?, slot_10 = ? " +
                "WHERE player_id = ?";
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
        String query = "UPDATE player_magic_tree SET is_upgrade = ?, level = ?, curr_pea = ? WHERE player_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setBoolean(1, false);
            statement.setInt(2, 1);
            statement.setInt(3, 0);
            statement.setInt(4, player.getId());
            statement.executeUpdate();
        }
    }

    private void savePlayerInventory(Player player, Connection connection) throws SQLException {
    }
}
