package nro.server.service.repositories.player;

import lombok.Getter;
import nro.server.service.model.item.Item;
import nro.server.system.LogServer;
import nro.server.service.core.item.ItemFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("ALL")
public class PlayerCreator {

    @Getter
    private static final PlayerCreator instance = new PlayerCreator();

    public boolean createPlayer(Connection connection, int accountId, String name, byte gender, int hair) throws SQLException {
        var ms = System.currentTimeMillis();
        try {
            connection.setAutoCommit(false);
            int playerId = this.createPlayerBase(connection, accountId, name, gender, hair);
            if (playerId > 0) {

                this.createCurrenciesPlayer(connection, playerId);
                this.createLocationPlayer(connection, playerId, gender);
                this.createPlayerPoint(connection, playerId, gender);
                this.createPlayerSkillsShortCut(connection, playerId, gender);
                this.createMagicTreePlayer(connection, playerId);
                this.createPlayerInventory(connection, playerId, gender);
                this.createPlayerDataTask(connection, playerId);
                this.createPlayerSkills(connection, playerId, gender);
                this.createAdministratorPlayer(connection, playerId);
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
        // var ms = System.currentTimeMillis();
        int playerId;
        try (CallableStatement stmt = connection.prepareCall("{CALL `CreatePlayerBase`(?, ?, ?, ?, ?, ?, ?)}")) {
            stmt.setInt(1, accountId);
            stmt.setString(2, name);
            stmt.setByte(3, gender);
            stmt.setInt(4, head);
            stmt.setInt(5, 20);// item bag size
            stmt.setInt(6, 20);// item box size
            stmt.registerOutParameter(7, java.sql.Types.INTEGER);
            stmt.execute();
            playerId = stmt.getInt(7);
        }

        if (playerId == 0) {
            throw new SQLException("Failed to create player.");
        }
        return playerId;
    }

    private void createPlayerSkills(Connection connection, int playerId, int gender) throws SQLException {
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
            statement.executeBatch();
        }
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

    private void createAdministratorPlayer(Connection connection, int playerId) throws SQLException {
        String query = "INSERT INTO player_administrator (player_id) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, playerId);
            if (statement.executeUpdate() == 0) {
                LogServer.LogException("No rows were inserted into player_administrator for playerId: " + playerId);
                throw new SQLException("Failed to insert player administrator.");
            }
        }
    }

    private void createPlayerPoint(Connection connection, int playerId, byte gender) throws SQLException {
        String query = "INSERT INTO player_point (player_id, " +
                "hp, hp_default, hp_max, hp_current, " +
                "mp, mp_default, mp_max, mp_current, " +
                "dame, dame_max, dame_default, " +
                "stamina, max_stamina, " +
                "crit, crit_default, " +
                "defense, def_default, " +
                "power, limit_power, " +
                "tiem_nang, nang_dong) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

    private List<Item> createEmptyItems(int count) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.add(ItemFactory.getInstance().createItemNull());
        }
        return items;
    }

    private void createPlayerInventory(Connection connection, int playerId, byte gender) throws SQLException {
        List<Item> itemsBody = ItemFactory.getInstance().initializePlayerItems(gender);
        List<Item> itemsBag = createEmptyItems(20);
        List<Item> itemsBox = ItemFactory.getInstance().initItemBox();
        ensureItemSlots(itemsBody, 11);
        ensureItemSlots(itemsBag, 20);
        ensureItemSlots(itemsBox, 20);
        insertItemsToDatabase(connection, playerId, "player_items_body", itemsBody);
        insertItemsToDatabase(connection, playerId, "player_items_bag", itemsBag);
        insertItemsToDatabase(connection, playerId, "player_items_box", itemsBox);
    }

    private void ensureItemSlots(List<Item> items, int requiredSize) {
        while (items.size() < requiredSize) {
            items.add(ItemFactory.getInstance().createItemNull());
        }
    }

    private void insertItemsToDatabase(Connection connection, int playerId, String tableName, List<Item> items) throws SQLException {
        String query = "INSERT INTO " + tableName + " (player_id, row_index, temp_id, quantity, options) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (int index = 0; index < items.size(); index++) {
                Item item = items.get(index);
                statement.setInt(1, playerId);
                statement.setInt(2, index);
                statement.setInt(3, item.getTemplate() == null ? -1 : item.getTemplate().id());
                statement.setInt(4, item.getQuantity());
                statement.setString(5, item.getJsonOptions());
                statement.addBatch();
            }
            int[] rowsAffected = statement.executeBatch();
            if (Arrays.stream(rowsAffected).sum() == 0) {
                throw new SQLException("Failed to insert items into " + tableName);
            }
        }
    }

    private void createPlayerDataTask(Connection connection, int playerId) throws SQLException {
        String query = "INSERT INTO player_task (player_id, task_id, task_index, task_count) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, playerId);
            statement.setInt(2, 0); // task_id
            statement.setInt(3, 0); // task_index
            statement.setInt(4, 0); // task_count
            if (statement.executeUpdate() == 0) {
                LogServer.LogException("No rows were inserted into player_task for playerId: " + playerId);
                throw new SQLException("Failed to insert player task.");
            }
        } catch (SQLException ex) {
            throw ex;
        }
    }


    private void createPlayerSkillsShortCut(Connection connection, int playerId, int gender) throws SQLException {

        String query = "INSERT INTO player_skills_shortcut (player_id, slot_1, slot_2, slot_3, slot_4, slot_5, slot_6, slot_7, slot_8, slot_9, slot_10) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            int idSkill = (gender == 0) ? 0 : (gender == 1) ? 2 : 4;

            statement.setInt(1, playerId);
            statement.setInt(2, idSkill); // skill_id

            for (int i = 3; i <= 11; i++) {
                statement.setInt(i, -1);
            }

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to create player skills shortcut.");
            }
        } catch (SQLException e) {
            LogServer.LogException("Lỗi khi tạo shortcut skill: " + e.getMessage());
            throw e;
        }
    }

}
