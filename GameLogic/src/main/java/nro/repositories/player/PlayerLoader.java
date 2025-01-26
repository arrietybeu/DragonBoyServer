package nro.repositories.player;

import lombok.Getter;
import nro.model.player.Player;
import nro.network.Session;
import nro.repositories.DatabaseConnectionPool;
import nro.server.LogServer;
import nro.server.config.ConfigDB;

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
                        return this.mapResultSetToPlayer(session, resultSet);
                    }
                }
            }
        } catch (SQLException e) {
            LogServer.LogException("Error loading player for account_id: " + session.getUserInfo().getId() + ", Error: " + e.getMessage());
        }
        return null;
    }

    // Utility method to map ResultSet data to a Player object
    private Player mapResultSetToPlayer(Session session, ResultSet resultSet) throws SQLException {
        Player player = new Player(session);
        player.setId(resultSet.getInt("id"));
        player.setName(resultSet.getString("name"));
        return player;
    }

}
