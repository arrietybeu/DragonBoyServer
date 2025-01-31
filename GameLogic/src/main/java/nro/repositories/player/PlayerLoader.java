package nro.repositories.player;

import lombok.Getter;
import nro.model.map.GameMap;
import nro.model.player.Player;
import nro.network.Session;
import nro.repositories.DatabaseConnectionPool;
import nro.server.LogServer;
import nro.server.config.ConfigDB;
import nro.server.manager.MapManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerLoader {

    @Getter
    private static final PlayerLoader instance = new PlayerLoader();

    public Player loadPlayer(Session session) {
        String query = "SELECT * FROM player WHERE account_id = ? LIMIT 1";
        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_DYNAMIC)) {
            assert connection != null : "Connection is null";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, session.getUserInfo().getId());

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return this.mapResultSetToPlayer(session, resultSet, connection);
                    }
                }
            }
        } catch (SQLException e) {
            LogServer.LogException("Error loading player for account_id: " + session.getUserInfo().getId() + ", Error: " + e.getMessage());
        }
        return null;
    }

    // Utility method to map ResultSet data to a Player object
    private Player mapResultSetToPlayer(Session session, ResultSet resultSet, Connection connection) throws SQLException {
        Player player = new Player(session);
        player.setId(resultSet.getInt("id"));
        player.setName(resultSet.getString("name"));
        player.setGender(resultSet.getByte("gender"));

        // Load player currencies
        this.loadPlayerCurrencies(player, connection);

        // Load player point
        this.loadPlayerPoint(player, connection);

        // Load player location
        this.loadPlayerLocation(player, connection);

        // Load player magic tree
        this.loadPlayerMagicTree(player, connection);
        return player;
    }

    private void loadPlayerCurrencies(Player player, Connection connection) throws SQLException {
        String query = "SELECT gold, gem, ruby FROM player_currencies WHERE player_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, player.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    player.getPlayerCurrencies().setGold(resultSet.getInt("gold"));
                    player.getPlayerCurrencies().setGem(resultSet.getInt("gem"));
                    player.getPlayerCurrencies().setRuby(resultSet.getInt("ruby"));
                }
            }
        }
    }

    private void loadPlayerPoint(Player player, Connection connection) throws SQLException {
        String query = "SELECT hp, mp, dame_default, stamina, power, limit_power FROM player_point WHERE player_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, player.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
//                    player.setHp(resultSet.getInt("hp"));
//                    player.setMp(resultSet.getInt("mp"));
//                    player.setDamage(resultSet.getInt("dame_default"));
//                    player.setStamina(resultSet.getInt("stamina"));
//                    player.setPower(resultSet.getInt("power"));
//                    player.setLimitPower(resultSet.getInt("limit_power"));
                }
            }
        }
    }

    private void loadPlayerLocation(Player player, Connection connection) throws SQLException {
        String query = "SELECT pos_x, pos_y, map_id FROM player_location WHERE player_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, player.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    player.setX(resultSet.getShort("pos_x"));
                    player.setY(resultSet.getShort("pos_y"));

                    int mapID = resultSet.getInt("map_id");
                    GameMap gameMap = MapManager.getInstance().findMapById(mapID);
                }
            }
        }
    }

    private void loadPlayerMagicTree(Player player, Connection connection) throws SQLException {
        String query = "SELECT is_upgrade, level, curr_pea FROM player_magic_tree WHERE player_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, player.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
//                    player.setMagicTreeUpgrade(resultSet.getBoolean("is_upgrade"));
//                    player.setMagicTreeLevel(resultSet.getInt("level"));
//                    player.setMagicTreePea(resultSet.getInt("curr_pea"));
                }
            }
        }
    }

    private void loadPLayerDataTask(Player player, Connection connection) throws SQLException {
        String query = "SELECT * FROM player_task WHERE player_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, player.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
//                    var taskMain = new TaskMain();
//                    taskMain.id = resultSet.getInt("id");
//                    taskMain.index = resultSet.getInt("index");
//                    taskMain.name = resultSet.getString("name");
//                    taskMain.detail = resultSet.getString("detail");
//                    player.getTasks().add(taskMain);
                }
            }
        }
    }
}
