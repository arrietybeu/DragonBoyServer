package nro.server.dao;

import com.artemis.Entity;
import com.artemis.World;
import nro.commons.database.Database;
import nro.commons.database.DatabaseFactory;
import nro.server.configs.main.ConfigCharacter;
import nro.server.engine.GameWorld;
import nro.server.model.ecs.component.*;
import nro.server.model.ecs.component.player.CurrencyComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * @author Arriety
 */
public class PlayerDAO {

    private static final Logger log = LoggerFactory.getLogger(PlayerDAO.class);

    private static final String QUERY_CALL_CREATE_PLAYER = "{CALL `CreatePlayerBase`(?, ?, ?, ?, ?, ?, ?, ?)}";
    private static final String QUERY_NAME_TAKEN = "SELECT 1 FROM player WHERE name = ? LIMIT 1";
    private static final String QUERY_ACCOUNT_HAS_CHARACTER = "SELECT 1 FROM player WHERE account_id = ? LIMIT 1";

    public static boolean isNameTaken(Connection conn, String name) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(QUERY_NAME_TAKEN)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static boolean accountHasCharacter(Connection conn, int accountId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(QUERY_ACCOUNT_HAS_CHARACTER)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static boolean saveNewPlayer(Connection conn, int accountId, String name, byte gender, int hair) {
        try {
            int playerId = createPlayerBase(conn, accountId, name, gender, hair);
            if (playerId <= 0) {
                log.error("Failed to create player base for account ID: {} with name: {}", accountId, name);
                return false;
            }
            if (!createDefaultCurrencies(conn, playerId)) return false;
            if (!createDefaultLocation(conn, playerId, gender)) return false;
            if (!createDefaultPoints(conn, playerId, gender)) return false;
            if (!createDefaultMagicTree(conn, playerId)) return false;
            return createDefaultSkillShortcuts(conn, playerId, gender);
        } catch (SQLException e) {
            log.error("Failed to create player base for account ID: {} with name: {}. Error: {}", accountId, name, e.getMessage());
            return false;
        }
    }

    private static boolean createDefaultCurrencies(Connection conn, int playerId) throws SQLException {
        String sql = "INSERT INTO player_currencies (player_id, gold, gem, ruby) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ps.setInt(2, 2000); // gold
            ps.setInt(3, 0);    // gem
            ps.setInt(4, 50);   // ruby
            if (ps.executeUpdate() <= 0) {
                log.error("No rows were inserted into player_currencies for playerId: {}", playerId);
                return false;
            }
        }
        return true;
    }

    private static boolean createDefaultMagicTree(Connection connection, int playerId) throws SQLException {
        String query = "INSERT INTO player_magic_tree (player_id, is_upgrade, time_upgrade, level, time_harvest, curr_pea) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, playerId);
            statement.setInt(2, 0); // is_upgrade
            statement.setLong(3, System.currentTimeMillis()); // time_upgrade
            statement.setInt(4, 1); // level
            statement.setLong(5, System.currentTimeMillis()); // time_harvest
            statement.setInt(6, 5); // curr_pea
            if (statement.executeUpdate() <= 0) {
                log.error("No rows were inserted into player_magic_trêe for playerId: {}", playerId);
                return false;
            }
        }
        return true;
    }

    private static boolean createDefaultLocation(Connection conn, int playerId, byte gender) throws SQLException {
        String sql = "INSERT INTO player_location (player_id, pos_x, pos_y, map_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ps.setInt(2, 100); // pos_x
            ps.setInt(3, 384); // pos_y
            ps.setInt(4, 39 + gender); // map_id
            if (ps.executeUpdate() <= 0) {
                log.error("No rows were inserted into player_location for playerId: {}", playerId);
                return false;
            }
        }
        return true;
    }

    private static boolean createDefaultPoints(Connection connection, int playerId, byte gender) throws SQLException {
        String query = "INSERT INTO player_point (player_id, " + "hp, hp_default, hp_max, hp_current, " + "mp, mp_default, mp_max, mp_current, " + "dame, dame_max, dame_default, " + "stamina, max_stamina, " + "crit, crit_default, " + "defense, def_default, " + "power, limit_power, " + "tiem_nang, nang_dong) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            int index = 1;
            statement.setInt(index++, playerId);

            // HP: cHPGoc, hp_default, hp_max (cHPFull), hp_current (cHP)
            statement.setLong(index++, 100L);  // hp: cHPGoc = 100
            statement.setLong(index++, 100L);  // hp_default = 100
            statement.setLong(index++, 120L);  // hp_max = cHPFull = 120
            statement.setLong(index++, 120L);  // hp_current = cHP = 120

            // MP: cMPGoc, mp_default, mp_max (cMPFull), mp_current (cMP)
            statement.setLong(index++, 100L);  // mp: cMPGoc = 100
            statement.setLong(index++, 100L);  // mp_default = 100
            statement.setLong(index++, 100L);  // mp_max = cMPFull = 100
            statement.setLong(index++, 100L);  // mp_current = cMP = 100

            // Damage: cDamGoc, dame_max (cDamFull), dame_default
            statement.setLong(index++, 15L);   // dame: cDamGoc = 15
            statement.setLong(index++, 15L);   // dame_max: cDamFull = 15
            statement.setLong(index++, 15L);   // dame_default = 15

            // Stamina
            statement.setInt(index++, 1000);   // stamina
            statement.setInt(index++, 1000);   // max_stamina

            // Critical: cCriticalGoc và cCriticalFull
            statement.setByte(index++, (byte) 0); // crit: cCriticalGoc = 0
            statement.setInt(index++, 0);         // crit_default: cCriticalFull = 0

            // Defense: cDefGoc và cDefull
            statement.setInt(index++, 0);    // defense: cDefGoc = 0
            statement.setLong(index++, 0);  // def_default: cDefull = 3

            // Power và Limit Power (expForOneAdd)
            statement.setLong(index++, 2000L); // power = 2000 (giữ nguyên theo cũ)
            statement.setInt(index++, 100);    // limit_power = expForOneAdd = 100

            // Tiem nang và Nang dong
            statement.setLong(index++, 1200L); // tiem_nang: cTiemNang = 1200
            statement.setInt(index++, 0);      // nang_dong

            if (statement.executeUpdate() <= 0) {
                log.error("No rows were inserted into player_point for playerId: {}", playerId);
                return false;
            }
        }
        return true;
    }

    private static boolean createDefaultInventory(Connection connection, int playerId) throws SQLException {
        String query = "INSERT INTO player_inventory (player_id, item_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Example: Insert a default item (e.g., Item ID 1 with quantity 1)
            statement.setInt(1, playerId);
            statement.setInt(2, 1); // Default item ID
            statement.setInt(3, 1); // Default quantity
            if (statement.executeUpdate() <= 0) {
                log.error("No rows were inserted into player_inventory for playerId: {}", playerId);
                return false;
            }
        }
        return true;
    }

    private static boolean createDefaultSkillShortcuts(Connection connection, int playerId, int gender) throws SQLException {
        String query = "INSERT INTO player_skills_shortcut (player_id, slot_1) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            int idSkill = (gender == 0) ? 0 : (gender == 1) ? 2 : 4;

            statement.setInt(1, playerId);
            statement.setInt(2, idSkill); // skill_id

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected <= 0) {
                log.error("No rows were inserted into player_skills_shortcut for playerId: {}", playerId);
                return false;
            }
        }
        return true;
    }

    private static int createPlayerBase(Connection connection, int accountId, String name, byte gender, int head) throws SQLException {
        // var ms = System.currentTimeMillis();
        int playerId;
        try (CallableStatement stmt = connection.prepareCall(QUERY_CALL_CREATE_PLAYER)) {
            stmt.setInt(1, accountId);
            stmt.setString(2, name);
            stmt.setByte(3, gender);
            stmt.setInt(4, head);
            stmt.setInt(5, ConfigCharacter.INVENTORY_BODY_SIZE);// item body size
            stmt.setInt(6, ConfigCharacter.INVENTORY_BAG_SIZE);// item bag size
            stmt.setInt(7, ConfigCharacter.INVENTORY_BOX_SIZE);// item box size
            stmt.registerOutParameter(8, java.sql.Types.INTEGER);
            stmt.execute();
            playerId = stmt.getInt(8);
        }

        if (playerId <= 0) {
            throw new SQLException("Failed to create player.");
        }
        return playerId;
    }

    public static int loadPlayerEntity(int playerId, int accountId) {
        World world = GameWorld.getInstance().getWorld();
        int playerEntityID = world.create();

        InfoComponent info = new InfoComponent();
        info.id = playerId;
        info.accountId = accountId;

        world.edit(playerEntityID).add(info);

        var playerEntity = world.getEntity(playerEntityID);

        try (Connection conn = DatabaseFactory.getConnection()) {
            loadPlayerInfo(conn, playerEntity, playerId);
            loadPlayerLocation(conn, playerEntity, playerId);
            loadPlayerStatsAndHealth(conn, playerEntity, playerId);
            loadPlayerCurrencies(conn, playerEntity, playerId);
            InventoryDAO.loadInventoryForPlayer(conn, playerId);
            log.info("Successfully loaded entity for player ID: {}", playerId);
            return playerEntityID;
        } catch (Exception e) {
            log.error("Failed to load entity for player ID: {}. Rolling back.", playerId, e);
            world.delete(playerEntityID);
            return -1;
        }
    }

    public static int findPlayerIdByAccountId(int accountId) {
        final int[] playerId = {-1};
        String sql = "SELECT id FROM player WHERE account_id = ? LIMIT 1";
        Database.select(sql, rs -> {
            if (rs.next()) {
                playerId[0] = rs.getInt("id");
            }
        }, stmt -> stmt.setInt(1, accountId));
        return playerId[0];
    }

    private static void loadPlayerInfo(Connection conn, Entity entity, int playerId) throws SQLException {
        String sql = "SELECT name, gender, is_online FROM player WHERE id = ?";
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
                    entity.edit().add(new PositionComponent(rs.getInt("map_id"), rs.getShort("pos_x"), rs.getShort("pos_y")));
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
                    entity.edit().add(new StatsComponent(rs.getLong("power"), rs.getLong("tiem_nang"), (int) rs.getLong("hp"), (int) rs.getLong("mp"), rs.getInt("dame_default"), rs.getInt("defense"), rs.getByte("crit"))).add(new HealthComponent(rs.getLong("hp_current"), rs.getLong("hp_max"), rs.getLong("mp_current"), rs.getLong("mp_max")));
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
                    entity.edit().add(new CurrencyComponent(rs.getLong("gold"), rs.getInt("gem"), rs.getInt("ruby")));
                }
            }
        }
    }

    public static int[] getUsedIDs() {
        try (Connection con = DatabaseFactory.getConnection(); PreparedStatement stmt = con.prepareStatement("SELECT id FROM player", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ResultSet rs = stmt.executeQuery()) {
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
