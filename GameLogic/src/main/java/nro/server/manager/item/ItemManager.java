package nro.server.manager.item;

import lombok.Getter;
import nro.server.config.ConfigDB;
import nro.repositories.DatabaseConnectionPool;
import nro.server.manager.IManager;
import nro.model.item.ItemTemplate;
import nro.server.LogServer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class ItemManager implements IManager {

    @Getter
    private final List<ItemTemplate> itemTemplates = new ArrayList<>();
    @Getter
    private final List<ItemTemplate.ArrHead2Frames> arrHead2Frames = new ArrayList<>();
    @Getter
    private static final ItemManager instance = new ItemManager();

    @Override
    public void init() {
        this.loadItemTemplate();
        this.loadItemArrHead2Fr();
    }

    @Override
    public void reload() {
    }

    @Override
    public void clear() {
    }

    private void loadItemTemplate() {
        String sql = "SELECT * FROM `item_template`";
        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC)) {
            assert connection != null : "Connection is null";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 var resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    var id = resultSet.getShort("id");
                    var type = resultSet.getByte("type");
                    var gender = resultSet.getByte("gender");
                    var name = resultSet.getString("name");
                    var description = resultSet.getString("description");
                    var level = resultSet.getByte("level");
                    var powerRequire = resultSet.getInt("power_require");
                    var iconID = resultSet.getShort("icon_id");
                    var part = resultSet.getShort("part");
                    var isUpToUp = resultSet.getBoolean("is_up_top");

                    var itemTemplate = new ItemTemplate(id, type, gender, name, description, level, iconID, part, isUpToUp, powerRequire);
                    this.itemTemplates.add(itemTemplate);
                }
                LogServer.LogInit("ItemManager initialized size: " + itemTemplates.size());
            }
        } catch (Exception e) {
            LogServer.LogException("Error loadItem: " + e.getMessage());
        }
    }

    private void loadItemArrHead2Fr() {
        String sql = "SELECT id, head_one, head_two FROM `item_arr_head_2frame`";
        try (var connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC)) {
            assert connection != null : "Connection is null";
            try (var preparedStatement = connection.prepareStatement(sql);
                 var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var id = resultSet.getInt("id");
                    var head_one = resultSet.getInt("head_one");
                    var head_two = resultSet.getInt("head_two");

                    List<Integer> heads = new ArrayList<>();
                    heads.add(head_one);
                    heads.add(head_two);

                    this.arrHead2Frames.add(new ItemTemplate.ArrHead2Frames(id, heads));
                }
            }
        } catch (Exception e) {
            LogServer.LogException("Error loadItemArr_Head_2Fr: " + e.getMessage());
        }
        LogServer.LogInit("Item ArrHead2Frames initialized size: " + arrHead2Frames.size());
    }

    private void loadItemArrHeadMove() {
    }

    public ItemTemplate getItemTemplate(short id) {
        return itemTemplates.get(id);
    }

    public void logItemTemplate() {
        if (itemTemplates.isEmpty()) {
            throw new RuntimeException("ItemTemplates is empty");
        }
        for (ItemTemplate itemTemplate : this.itemTemplates) {
            LogServer.DebugLogic(itemTemplate.toString());
        }
    }

    public void logItemArrHead2Fr() {
        if (arrHead2Frames.isEmpty()) {
            throw new RuntimeException("ArrHead2Frames is empty");
        }
        for (ItemTemplate.ArrHead2Frames arrHead2Frames : this.arrHead2Frames) {
            LogServer.DebugLogic(arrHead2Frames.toString());
        }
    }
}
