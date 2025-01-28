package nro.repositories.player;

import lombok.Getter;
import nro.model.item.Item;
import nro.server.LogServer;
import nro.service.ItemService;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class PlayerCreator {

    @Getter
    private static final PlayerCreator instance = new PlayerCreator();

    private static final String INSERT_PLAYER_LOCATION =
            "INSERT INTO player_location (player_id, pos_x, pos_y, map_id) VALUES (?, ?, ?, ?)";

    private static final String INSERT_PLAYER_CURRENCIES =
            "INSERT INTO player_currencies (player_id, gold, gem, ruby) VALUES (?, ?, ?, ?)";

    private static final String INSERT_PLAYER_POINT =
            "INSERT INTO player_point (player_id, hp, hp_default, mp, mp_default, dame_default, stamina, max_stamina, crit_default, def_default, tiem_nang, power, limit_power, nang_dong) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String INSERT_PLAYER_MAGIC_TREE =
            "INSERT INTO player_magic_tree (player_id, is_upgrade, time_upgrade, level, time_harvest, curr_pea) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String INSERT_PLAYER_ITEM_BODY =
            "INSERT INTO player_items_body (player_id, temp_id, quantity, create_time, options) VALUES (?, ?, ?, ?, ?)";

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

                connection.commit();
                LogServer.DebugLogic("Time create player: " + (System.currentTimeMillis() - ms) + " ms");
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

        return playerId;
    }

    private void createLocationPlayer(Connection connection, int playerId, byte gender) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_PLAYER_LOCATION)) {
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
        try (PreparedStatement statement = connection.prepareStatement(INSERT_PLAYER_CURRENCIES)) {
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
        try (PreparedStatement statement = connection.prepareStatement(INSERT_PLAYER_POINT)) {
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
        try (PreparedStatement statement = connection.prepareStatement(INSERT_PLAYER_MAGIC_TREE)) {
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
        try (PreparedStatement statement = connection.prepareStatement(INSERT_PLAYER_ITEM_BODY)) {
            List<Item> items = this.initializePlayerItems(gender);
            for (Item item : items) {
//                statement.setInt(1, playerId);
//                statement.setInt(2, item.getTempId());
//                statement.setInt(3, item.getQuantity());
//                statement.setLong(4, System.currentTimeMillis());
//                statement.setString(5, item.getOptions());
            }
            int rows = statement.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Failed to create item body player.");
            }
        }
    }

    public static ArrayList<Item> initializePlayerItems(byte clazz) throws RuntimeException {
        ArrayList<Item> items = new ArrayList<>();

        // class[0] = trai dat,
        // class[1] = namec,
        // class[2] = xayda,

        short[][] itemIdsByClass = {
                {0, 6},
                {1, 7},
                {2, 8}
        };

        if (clazz < 0 || clazz > itemIdsByClass.length) {
            return items;
        }

        short[] itemIds = itemIdsByClass[clazz];

        for (short itemId : itemIds) {
            Item item = createAndInitItem(itemId);
            if (item != null) {
                items.add(item);
            } else {
                throw new RuntimeException("Failed to create item id: " + itemId);
            }
        }
        return items;
    }

    private static Item createAndInitItem(short itemId) {
        Item item = ItemService.getInstance().createItem(itemId, 1);
        ItemService.getInstance().initBaseOptions(item);
        return item;
    }

}
