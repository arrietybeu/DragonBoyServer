package nro.server.dao;

import com.artemis.Entity;
import com.artemis.World;
import nro.commons.database.Database;
import nro.commons.database.DatabaseFactory;
import nro.server.configs.main.ConfigCharacter;
import nro.server.engine.GameWorld;
import nro.server.model.ecs.component.*;
import nro.server.model.ecs.component.player.CurrencyComponent;
import nro.server.model.ecs.component.player.QuestInstanceComponent;
import nro.server.utils.MapUtils;
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
            if (!createDefaultSkills(conn, playerId, gender)) return false;
            if (!createDefaultTask(conn, playerId)) return false;
            InventoryDAO.createPlayerInventory(conn, playerId, gender);
            return createDefaultSkillShortcuts(conn, playerId, gender);
        } catch (SQLException e) {
            log.error("Failed to create player base for account ID: {} with name: {}. Error: {}", accountId, name, e.getMessage(), e);
            return false;
        }
    }

    private static boolean createDefaultCurrencies(Connection conn, int playerId) throws SQLException {
        String sql = "INSERT INTO player_currencies (player_id, gold, gem, ruby) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ps.setInt(2, ConfigCharacter.CREATION_GOLD); // gold
            ps.setInt(3, ConfigCharacter.CREATION_GEM);    // gem
            ps.setInt(4, ConfigCharacter.CREATION_RUBY);   // ruby
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

    private static boolean createDefaultTask(Connection con, int playerId) throws SQLException {
        String query = "INSERT INTO player_task (player_id, task_id, task_index, task_count) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, playerId);
            statement.setInt(2, ConfigCharacter.CREATION_TASK_START_ID); // task_id
            statement.setInt(3, ConfigCharacter.CREATION_TASK_START_INDEX); // task_index
            statement.setInt(4, ConfigCharacter.CREATION_TASK_START_COUNT); // task_count
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected <= 0) {
                log.error("No rows were inserted into player_task for playerId: {}", playerId);
                return false;
            }
        }
        return true;
    }

    private static boolean createDefaultSkills(Connection connection, int playerId, int gender) throws SQLException {
        String query = "INSERT INTO player_skills (player_id, skill_id," +
                " current_level, last_time_use_skill) VALUES (?, ?, ?, ?);";

        int[] skills = gender == 0 ? new int[]{0, 1, 6, 9, 10, 20, 22, 19, 24}
                : gender == 1 ? new int[]{2, 3, 7, 11, 12, 17, 18, 19, 26}
                : new int[]{4, 5, 8, 13, 14, 21, 23, 19, 25};

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (int i = 0; i < skills.length; i++) {
                statement.setInt(1, playerId);
                statement.setInt(2, skills[i]);
                statement.setInt(3, (i == 0) ? 1 : 0);
                statement.setLong(4, 0);
                statement.addBatch();
            }
            int[] rowsAffected = statement.executeBatch();
            if (rowsAffected.length != skills.length) {
                log.error("Not all skills were inserted for playerId: {}", playerId);
                return false;
            }
        }
        return true;
    }

    private static int createPlayerBase(Connection connection, int accountId, String name, byte gender, int head) throws SQLException {
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

        if (playerId <= 0)
            throw new SQLException("Failed to create player.");
        return playerId;
    }

    private static final int ________________LOAD_PLAYER_ENTITY________________ = -1;

    public static Entity loadPlayerEntity(int playerId, int accountId) {
        World world = GameWorld.getInstance().getWorld();
        int playerEntityID = GameWorld.getInstance().createEntity();

        InfoComponent info = new InfoComponent();
        info.id = playerId;
        info.accountId = accountId;

        var playerEntity = world.getEntity(playerEntityID);

        var edit = world.edit(playerEntityID);
        edit.add(info);
        edit.add(new AppearanceComponent());
        edit.add(new StateComponent());
        edit.add(new BuffComponent());

        // cai nay luon o cuoi cung
        edit.add(new FusionComponent());

        try (Connection conn = DatabaseFactory.getConnection()) {
            loadPlayerInfo(conn, playerEntity, playerId);
            loadPlayerLocation(conn, playerEntity, playerId);
            loadPlayerStatsAndHealth(conn, playerEntity, playerId);
            loadPlayerCurrencies(conn, playerEntity, playerId);
            loadPlayerSkills(conn, playerEntity, playerId);
            loadPlayerTask(conn, playerEntity, playerId);

            // inventory nên load cuối cùng chắc vậy
            InventoryDAO.loadInventoryForPlayer(conn, playerEntity, playerId);

            log.info("Successfully loaded entity for player ID: {}", playerId);
            return playerEntity;
        } catch (Exception e) {
            log.error("Failed to load entity for player ID: {}. Rolling back.", playerId, e);
            world.delete(playerEntityID);
            return null;
        }
    }

    private static void loadPlayerInfo(Connection conn, Entity entity, int playerId) throws SQLException {
        String sql = "SELECT name, gender, is_online, created_at, max_bag_size, max_box_size, head FROM player WHERE id = ?";
        InfoComponent info = entity.getComponent(InfoComponent.class);
        var appearance = entity.getComponent(AppearanceComponent.class);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    info.name = rs.getString("name");
                    info.gender = rs.getByte("gender");
                    info.isOnline = rs.getBoolean("is_online");
                    info.createdAt = rs.getTimestamp("created_at").toInstant();
                    info.maxBagSize = rs.getByte("max_bag_size");
                    info.maxBoxSize = rs.getByte("max_box_size");
                    appearance.headDefault = rs.getByte("head");
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

                    var mapId = rs.getShort("map_id");
                    var x = rs.getShort("pos_x");
                    var y = rs.getShort("pos_y");

                    var position = MapUtils.createPosition(mapId, entity.getId(), x, y);

                    if (position == null)
                        throw new SQLException("Khong load duoc position cua World: " + mapId);

                    entity.edit().add(new PositionComponent(position.mapId(), position.x(), position.y(), position.zoneID()));
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

                    long hp = rs.getLong("hp");
                    int hp_default = rs.getInt("hp_default");
                    long hp_max = rs.getLong("hp_max");
                    long hp_current = rs.getLong("hp_current");

                    long mp = rs.getLong("mp");
                    int mp_default = rs.getInt("mp_default");
                    long mp_max = rs.getLong("mp_max");
                    long mp_current = rs.getLong("mp_current");

                    long dame = rs.getLong("dame");
                    long dame_max = rs.getLong("dame_max");
                    int dame_default = rs.getInt("dame_default");

                    int stamina = rs.getInt("stamina");
                    int max_stamina = rs.getInt("max_stamina");

                    byte crit = rs.getByte("crit");
                    byte crit_default = rs.getByte("crit_default");

                    int defense = rs.getInt("defense");
                    int def_default = rs.getInt("def_default");

                    long power = rs.getLong("power");
                    long limit_power = rs.getLong("limit_power");
                    long tiem_nang = rs.getLong("tiem_nang");

                    int nang_dong = rs.getInt("nang_dong");

                    var stats = new StatsComponent(power, tiem_nang, hp_default, mp_default,
                            dame_default, def_default, crit_default, nang_dong);
                    var health = new HealthComponent(hp_current, hp_max, mp_current, mp_max);
                    entity.edit().add(stats).add(health);
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

    private static void loadPlayerSkills(Connection conn, Entity entity, int playerId) throws SQLException {
        String sql = "SELECT * FROM player_skills_shortcut WHERE player_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            try (var resultSet = ps.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("Không tìm thấy skill short cut cho player id: " + playerId);
                }

                byte[] skillShortCut = new byte[10];

                for (int i = 0; i < 10; i++) {
                    skillShortCut[i] = resultSet.getByte("slot_" + (i + 1));
                }

                var skillShortCutComponent = new SkillComponent();
                skillShortCutComponent.skillShortCut = skillShortCut;
                entity.edit().add(skillShortCutComponent);
            }
        }
    }

    private static void loadPlayerTask(Connection conn, Entity entity, int playerId) throws SQLException {
        String sql = "SELECT task_id, task_index, task_count FROM player_task WHERE player_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, playerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                QuestInstanceComponent quest = entity.edit().create(QuestInstanceComponent.class);
                quest.questId = rs.getInt("task_id");
                quest.currentStep = rs.getInt("task_index");
                quest.currentCount = rs.getInt("task_count");
                quest.completed = false;
            }
        }
    }

    private static final int ________________SAVE_PLAYER_ENTITY________________ = -1;

    public static boolean savePlayerEntity(Entity entity) {

        Database.withConnection(connection -> {
            savePlayerLocation(connection, entity, entity.getComponent(InfoComponent.class).id);
            return null;
        });
        return false;
    }

    private static void savePlayerLocation(Connection conn, Entity entity, int playerId) throws SQLException {
        PositionComponent pos = entity.getComponent(PositionComponent.class);
        if (pos == null) return;

        final String sql =
                "INSERT INTO player_location (player_id, pos_x, pos_y, map_id) " +
                        "VALUES (?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE pos_x = VALUES(pos_x), pos_y = VALUES(pos_y), map_id = VALUES(map_id)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ps.setInt(2, pos.x);
            ps.setInt(3, pos.y);
            ps.setInt(4, pos.mapId);
            ps.executeUpdate();
        }
    }

    private static final int ________________SUPPORT________________ = -1;

    public static int[] getUsedIDs() {
        try (Connection con = DatabaseFactory.getConnection(); PreparedStatement stmt = con.prepareStatement("SELECT id FROM player",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ResultSet rs = stmt.executeQuery()) {
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
}
