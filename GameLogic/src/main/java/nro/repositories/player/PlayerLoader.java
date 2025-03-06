package nro.repositories.player;

import lombok.Getter;
import nro.model.item.Item;
import nro.model.map.areas.Area;
import nro.model.player.Player;
import nro.model.player.PlayerInventory;
import nro.model.player.PlayerMagicTree;
import nro.model.player.PlayerPoints;
import nro.model.task.TaskMain;
import nro.model.template.entity.SkillInfo;
import nro.server.network.Session;
import nro.repositories.DatabaseConnectionPool;
import nro.server.LogServer;
import nro.server.config.ConfigDB;
import nro.server.manager.MapManager;
import nro.server.manager.TaskManager;
import nro.server.manager.skill.SkillManager;
import nro.service.core.ItemFactory;

import java.sql.*;
import java.time.Instant;
import java.util.List;

@SuppressWarnings("ALL")
public class PlayerLoader {

    @Getter
    private static final PlayerLoader instance = new PlayerLoader();

    public Player loadPlayer(Session session) throws Exception {
        String query = "SELECT * FROM player WHERE account_id = ? LIMIT 1";
        var ms = System.currentTimeMillis();
        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_DYNAMIC)) {
            if (connection == null) {
                throw new Exception("Error loading player for account_id: " + session.getUserInfo().getId() + ", Error: connection null");
            }
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, session.getUserInfo().getId());

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return this.mapResultSetToPlayer(session, resultSet, connection);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error loading p layer for account_id: " + session.getUserInfo().getId() + ", Error: " + e.getMessage());
        } finally {
            var time = System.currentTimeMillis() - ms;
            LogServer.DebugLogic("Last time load PLayer: " + time + " ms");
        }
        return null;
    }

    // Utility method to map ResultSet data to a Player object
    private Player mapResultSetToPlayer(Session session, ResultSet resultSet, Connection connection) throws SQLException {
        Player player = new Player();
        player.setSession(session);

        player.setId(resultSet.getInt("id"));
        player.setName(resultSet.getString("name"));
        player.setGender(resultSet.getByte("gender"));
        player.getPlayerFashion().setHead(resultSet.getShort("head"));
        player.getPlayerInventory().setItemBagSize(resultSet.getByte("max_bag_size"));
        player.getPlayerInventory().setItemBoxSize(resultSet.getByte("max_box_size"));

        // load time create player
        Timestamp createdAtTimestamp = resultSet.getTimestamp("created_at");
        Instant createdAt = createdAtTimestamp.toInstant();
        player.setCreatedAt(createdAt);

        // Load player currencies
        this.loadPlayerCurrencies(player, connection);

        // Load player point
        this.loadPlayerPoint(player, connection);

        // Load player location
        this.loadPlayerLocation(player, connection);

        // Load player magic tree
        this.loadPlayerMagicTree(player, connection);

        // Load player data task
        this.loadPLayerDataTask(player, connection);

        // Load player skills shortcut
        this.loadPlayerSkillsShortCut(player, connection);

        this.loadPlayerSkills(player, connection);

        // Load player inventory
        this.loadPlayerInventory(player, connection);

        // Load Point
        player.getPlayerPoints().setPoint();

        return player;
    }

    private void loadPlayerSkills(Player player, Connection connection) throws SQLException {
        String query = "SELECT skill_id, current_level, last_time_use_skill FROM player_skills WHERE player_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, player.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    short skillId = resultSet.getShort("skill_id");
                    short currentLevel = resultSet.getShort("current_level");
                    // long lastTimeUseSkill = resultSet.getLong("last_time_use_skill");
                    if (currentLevel == 0) continue;
                    SkillInfo skillInfo = SkillManager.getInstance().getSkillInfo(skillId, player.getGender(), currentLevel);
                    if (skillInfo == null) continue;
                    player.getPlayerSkill().addSkill(skillInfo);
                }
            }
        }
    }

    private void loadPlayerInventory(Player player, Connection connection) throws SQLException {
        PlayerInventory playerInventory = player.getPlayerInventory();
        playerInventory.setItemBodySize(this.loadInventoryItems(player, connection, "player_items_body", playerInventory.getItemsBody()));
        playerInventory.setItemBagSize(this.loadInventoryItems(player, connection, "player_items_bag", playerInventory.getItemsBag()));
        playerInventory.setItemBoxSize(this.loadInventoryItems(player, connection, "player_items_box", playerInventory.getItemsBox()));
    }

    private int loadInventoryItems(Player player, Connection connection, String tableName, List<Item> inventory) throws SQLException {
        int maxRowIndex = 0;
        String query = "SELECT temp_id, quantity, create_time, options, row_index FROM " + tableName +
                " WHERE player_id = ? ORDER BY row_index ASC";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, player.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    short tempId = resultSet.getShort("temp_id");
                    int quantity = resultSet.getInt("quantity");
                    Timestamp timestamp = resultSet.getTimestamp("create_time");
                    long createTime = (timestamp != null) ? timestamp.getTime() : 0;
                    String optionsText = resultSet.getString("options");
                    int rowIndex = resultSet.getInt("row_index");

                    if (rowIndex > maxRowIndex) {
                        maxRowIndex = rowIndex + 1;
                    }
                    Item item = (tempId != -1) ? ItemFactory.getInstance().createItemNotOptionsBase(tempId, quantity) : ItemFactory.getInstance().createItemNull();
                    if (tempId != -1) {
                        item.setCreateTime(createTime);
                        item.setJsonOptions(optionsText);
                    }

                    while (inventory.size() <= rowIndex) {
                        inventory.add(ItemFactory.getInstance().createItemNull());
                    }

                    inventory.set(rowIndex, item);
                }
            }
        }

        return Math.max(maxRowIndex, inventory.size());
    }

    private void loadPlayerCurrencies(Player player, Connection connection) throws SQLException {
        String query = "SELECT gold, gem, ruby FROM player_currencies WHERE player_id = ? ";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, player.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    player.getPlayerCurrencies().setGold(resultSet.getInt("gold"));
                    player.getPlayerCurrencies().setGem(resultSet.getInt("gem"));
                    player.getPlayerCurrencies().setRuby(resultSet.getInt("ruby"));
                } else {
                    throw new SQLException("Khong tim thay currencies for player id: " + player.getId());
                }
            }
        }
    }

    private void loadPlayerPoint(Player player, Connection connection) throws SQLException {
        String query = "SELECT hp, hp_max, hp_current, "
                + "mp, mp_max, mp_current, "
                + "dame_max, dame_default, "
                + "crit, crit_default, "
                + "defense, def_default, "
                + "stamina, max_stamina, "
                + "power, limit_power, "
                + "tiem_nang, nang_dong "
                + "FROM player_point WHERE player_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, player.getId());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    PlayerPoints stats = player.getPlayerPoints();
                    // --- HP
                    stats.setBaseHP((int) rs.getLong("hp"));           // cHPGoc
                    stats.setMaxHP(rs.getLong("hp_max"));                // cHPFull
                    stats.setCurrentHp(rs.getLong("hp_current"));        // cHP

                    // --- MP
                    stats.setBaseMP((int) rs.getLong("mp"));             // cMPGoc
                    stats.setMaxMP(rs.getLong("mp_max"));                // cMPFull
                    stats.setCurrentMp(rs.getLong("mp_current"));        // cMP

                    // --- Damage
                    stats.setBaseDamage((int) rs.getLong("dame_default")); // cDamGoc
//                    stats.setTotalDamage(rs.getLong("dame_max"));          // cDamFull

                    // --- Critical Chance
                    stats.setBaseCriticalChance(rs.getByte("crit"));
                    stats.setTotalCriticalChance((byte) rs.getInt("crit_default"));

                    // --- Defense
                    stats.setBaseDefense(rs.getInt("defense"));
                    stats.setTotalDefense(rs.getLong("def_default"));

                    // --- Stamina
                    stats.setStamina(rs.getShort("stamina"));
                    stats.setMaxStamina(rs.getShort("max_stamina"));

                    // --- Potential Points & Power
                    stats.setPotentialPoints(rs.getLong("tiem_nang"));    // cTiemNang
                    stats.setPower(rs.getLong("power"));

                    // --- Exp per Stat Increase (expForOneAdd)
                    stats.setExpPerStatIncrease((short) rs.getInt("limit_power"));

                    // --- Các giá trị không lưu trong DB -> gán mặc định theo yêu cầu
                    stats.setMovementSpeed((byte) 5);         // cspeed = 5
                    stats.setHpPer1000Potential((byte) 20);     // hpFrom1000TiemNang = 20
                    stats.setMpPer1000Potential((byte) 20);     // mpFrom1000TiemNang = 20
                    stats.setDamagePer1000Potential((byte) 1);  // damFrom1000TiemNang = 1
                } else {
                    throw new SQLException("Không tìm thấy point cho player id: " + player.getId());
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
                    short x = resultSet.getShort("pos_x");
                    short y = resultSet.getShort("pos_y");
                    short mapID = resultSet.getShort("map_id");
                    if (x < 0 || y < 0 || player.getPlayerPoints().getCurrentHP() <= 0) {
                        player.getPlayerPoints().setCurrentHp(1);
                        x = 400;
                        y = 336;
                        mapID = (short) (21 + player.getGender());
                    }
                    player.setX(x);
                    player.setY(y);
                    Area gameMap = MapManager.getInstance().findMapById(mapID).getArea();
                    if (gameMap == null) {
                        throw new SQLException("Map not found for player location: " + mapID);
                    }
                    player.setArea(gameMap);
                } else {
                    throw new SQLException("Khong tim thay location for player id: " + player.getId());
                }
            }
        }
    }

    private void loadPlayerMagicTree(Player player, Connection connection) throws SQLException {
        String query = "SELECT is_upgrade, level, curr_pea, time_upgrade, time_harvest FROM player_magic_tree WHERE player_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, player.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    var isUpgrade = resultSet.getByte("is_upgrade") == 1;
                    var level = resultSet.getByte("level");
                    var currPea = resultSet.getByte("curr_pea");
                    var timeUpgrade = resultSet.getLong("time_upgrade");
                    var timeHarvest = resultSet.getLong("time_harvest");
                    PlayerMagicTree magicTree = player.getPlayerMagicTree();
                    magicTree.setUpgrade(isUpgrade);
                    magicTree.setLevel(level);
                    magicTree.setCurrPeas(currPea);
                    magicTree.setLastTimeUpgrade(timeUpgrade);
                    magicTree.setLastTimeHarvest(timeHarvest);
                } else {
                    throw new SQLException("Khong tim thay magic tree for player id: " + player.getId());
                }
            }
        }
    }

    private void loadPLayerDataTask(Player player, Connection connection) throws SQLException {
        String query = "SELECT * FROM player_task WHERE player_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, player.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    var taskId = resultSet.getInt("task_id");
                    var taskIndex = resultSet.getInt("task_index");
                    var taskCount = resultSet.getShort("task_count");
                    TaskMain taskMain = TaskManager.getInstance().getTaskMainById(taskId);
                    taskMain.getSubNameList().get(taskIndex).setCount(taskCount);
                    taskMain.setIndex(taskIndex);
                    player.getPlayerTask().setTaskMain(taskMain);
                } else {
                    throw new SQLException("Khong tim thay task for player id: " + player.getId());
                }
            }
        }
    }

    private void loadPlayerSkillsShortCut(Player player, Connection connection) throws SQLException {
        String query = "SELECT * FROM player_skills_shortcut WHERE player_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, player.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("Không tìm thấy skill short cut cho player id: " + player.getId());
                }

                byte[] skillShortCut = new byte[10];
                for (int i = 0; i < 10; i++) {
                    skillShortCut[i] = resultSet.getByte("slot_" + (i + 1));
                }
                player.getPlayerSkill().setSkillShortCut(skillShortCut);

                SkillInfo selectedSkill = null;
                for (byte skillId : skillShortCut) {
                    selectedSkill = player.getPlayerSkill().getSkillById(skillId);
                    if (selectedSkill != null) break;
                }

                if (selectedSkill == null) {
                    selectedSkill = player.getPlayerSkill().getSkillDefaultByGender(player.getGender());
                }
                player.getPlayerSkill().setSkillSelect(selectedSkill);
            }
        }
    }


}
