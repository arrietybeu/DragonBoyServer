package nro.server.dao;

import com.artemis.Entity;
import com.artemis.World;
import nro.commons.database.Database;
import nro.commons.database.DatabaseFactory;
import nro.server.engine.GameWorld;
import nro.server.model.ecs.component.*;
import nro.server.model.ecs.component.player.CurrencyComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Arriety
 */
public class PlayerDAO {

    private static final Logger log = LoggerFactory.getLogger(PlayerDAO.class);

    public static Entity loadPlayerEntity(int playerId, int accountId) {
        World world = GameWorld.getInstance().getWorld();
        Entity playerEntity = world.createEntity();

        InfoComponent info = new InfoComponent();
        info.id = playerId;
        info.accountId = accountId;
        world.edit(playerEntity.getId())
                .add(info);
        try (Connection conn = DatabaseFactory.getConnection()) {
            loadPlayerInfo(conn, playerEntity, playerId);
            loadPlayerLocation(conn, playerEntity, playerId);
            loadPlayerStatsAndHealth(conn, playerEntity, playerId);
            loadPlayerCurrencies(conn, playerEntity, playerId);
            log.info("Successfully loaded entity for player ID: {}", playerId);
            return playerEntity;
        } catch (Exception e) {
            log.error("Failed to load entity for player ID: {}. Rolling back.", playerId, e);
            world.deleteEntity(playerEntity);
            return null;
        }
    }

    public static int findPlayerIdByAccountId(int accountId) {
        final int[] playerId = {-1};
        String sql = "SELECT id FROM player WHERE account_id = ? LIMIT 1";
        Database.select(sql,
                rs -> {
                    if (rs.next()) {
                        playerId[0] = rs.getInt("id");
                    }
                },
                stmt -> stmt.setInt(1, accountId)
        );
        return playerId[0];
    }

    private static void loadPlayerInfo(Connection conn, Entity entity, int playerId) throws SQLException {
        String sql = "SELECT name, gender FROM player WHERE id = ?";
        InfoComponent info = entity.getComponent(InfoComponent.class);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    info.name = rs.getString("name");
                    info.gender = rs.getByte("gender");
                    info.isOnline = rs.getBoolean("is_online");
                }
            }
        }
    }

    private static void loadPlayerLocation(Connection conn, Entity entity, int playerId) throws SQLException {
        String sql = "SELECT map_id, pos_x, pos_y FROM player_location WHERE player_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    entity.edit().add(new PositionComponent(
                            rs.getInt("map_id"),
                            rs.getShort("pos_x"),
                            rs.getShort("pos_y")
                    ));
                }
            }
        }
    }

    private static void loadPlayerStatsAndHealth(Connection conn, Entity entity, int playerId) throws SQLException {
        String sql = "SELECT * FROM player_point WHERE player_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    entity.edit().add(new StatsComponent(
                            rs.getLong("power"),
                            rs.getLong("tiem_nang"),
                            (int) rs.getLong("hp"),
                            (int) rs.getLong("mp"),
                            rs.getInt("dame_default"),
                            rs.getInt("defense"),
                            rs.getByte("crit")
                    )).add(new HealthComponent(
                            rs.getLong("hp_current"),
                            rs.getLong("hp_max"),
                            rs.getLong("mp_current"),
                            rs.getLong("mp_max")
                    ));
                }
            }
        }
    }

    private static void loadPlayerCurrencies(Connection conn, Entity entity, int playerId) throws SQLException {
        String sql = "SELECT gold, gem, ruby FROM player_currencies WHERE player_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    entity.edit().add(new CurrencyComponent(
                            rs.getLong("gold"),
                            rs.getInt("gem"),
                            rs.getInt("ruby")
                    ));
                }
            }
        }
    }

    public static int[] getUsedIDs() {
        try (Connection con = DatabaseFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement("SELECT id FROM player", ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_READ_ONLY); ResultSet rs = stmt.executeQuery()) {
            rs.last();
            int count = rs.getRow();
            rs.beforeFirst();
            int[] ids = new int[count];
            for (int i = 0; rs.next(); i++)
                ids[i] = rs.getInt("id");
            return ids;
        } catch (SQLException e) {
            log.error("Can't get list of IDs from players table", e);
            return null;
        }
    }

}
