package nro.server.manager;

import lombok.Getter;
import nro.model.item.ItemOption;
import nro.model.item.ItemOptionTemplate;
import nro.network.Message;
import nro.server.config.ConfigDB;
import nro.repositories.DatabaseConnectionPool;
import nro.server.config.ConfigServer;
import nro.model.item.ItemTemplate;
import nro.server.LogServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ItemManager implements IManager {

    @Getter
    private static final ItemManager instance = new ItemManager();

    private static final byte ITEM_OPTION = 0;
    private static final byte ITEM_NORMAL = 1;
    private static final byte ITEM_ARR_HEAD_2FR = 100;
    private static final byte ITEM_ARR_HEAD_FLYMOVE = 101;

    private final Map<Short, ItemTemplate> itemTemplates = new HashMap<>();
    private final List<ItemTemplate.ArrHead2Frames> arrHead2Frames = new ArrayList<>();
    private final Map<Short, ItemOptionTemplate> itemOptionTemplates = new HashMap<>();

    private byte[] dataItemTemplate;
    private byte[] dataItemOption;
    private byte[] dataArrHead2Fr;

    @Override
    public void init() {
        this.loadItemTemplate();
        this.loadItemArrHead2Fr();
        this.loadItemOptionTemplate();
    }

    @Override
    public void reload() {
        this.clear();
        this.init();
    }

    @Override
    public void clear() {
        this.itemTemplates.clear();
        this.arrHead2Frames.clear();
        this.itemOptionTemplates.clear();
        this.dataItemTemplate = null;
        this.dataItemOption = null;
        this.dataArrHead2Fr = null;
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
                    var options = resultSet.getString("options");

                    List<ItemOption> itemOptions = new ArrayList<>();
                    JSONArray dataArray = (JSONArray) JSONValue.parse(options);
                    if (dataArray == null) {
                        throw new RuntimeException("Error load options item id: " + id);
                    }
                    for (Object o : dataArray) {
                        JSONArray opt = (JSONArray) o;
                        var idOption = Integer.parseInt(String.valueOf(opt.get(0)));
                        var param = Integer.parseInt(String.valueOf(opt.get(1)));
                        itemOptions.add(new ItemOption(idOption, param));
                    }

                    var itemTemplate = new ItemTemplate(
                            id,
                            type,
                            gender,
                            name,
                            description,
                            level,
                            iconID,
                            part,
                            isUpToUp,
                            powerRequire,
                            itemOptions
                    );
                    this.itemTemplates.put(id, itemTemplate);
                }
                this.setDataItemTemplate();
                LogServer.LogInit("ItemManager initialized size: " + itemTemplates.size());
            }
        } catch (Exception e) {
            LogServer.LogException("Error loadItem: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadItemOptionTemplate() {
        String query = "SELECT * FROM item_option_template";

        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC); PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                var id = rs.getInt("id");
                var name = rs.getString("name");
                var type = rs.getInt("type");
                ItemOptionTemplate itemOptionManager = new ItemOptionTemplate(id, name, type);
                this.itemOptionTemplates.put((short) id, itemOptionManager);
            }
            this.setItemOption();
            LogServer.LogInit("ItemOptionManager initialized size: " + this.itemOptionTemplates.size());
        } catch (Exception e) {
            LogServer.LogException("Error loadItemOptionTemplate: " + e.getMessage());
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
                this.setItemArrHead2fr();
            }
        } catch (Exception e) {
            LogServer.LogException("Error loadItemArr_Head_2Fr: " + e.getMessage());
        }
        LogServer.LogInit("Item ArrHead2Frames initialized size: " + arrHead2Frames.size());
    }

    private void setDataItemTemplate() {
        try (Message message = new Message()) {
            message.writer().writeByte(0);
            message.writer().writeByte(ConfigServer.VERSION_ITEM);
            message.writer().writeByte(ITEM_NORMAL);
            message.writer().writeShort(this.itemTemplates.size());
            for (ItemTemplate itemTemplate : this.itemTemplates.values()) {
                message.writer().writeByte(itemTemplate.type());
                message.writer().writeByte(itemTemplate.gender());
                message.writer().writeUTF(itemTemplate.name());
                message.writer().writeUTF(itemTemplate.description());
                message.writer().writeByte(itemTemplate.level());
                message.writer().writeInt(itemTemplate.strRequire());
                message.writer().writeShort(itemTemplate.iconID());
                message.writer().writeShort(itemTemplate.part());
                message.writer().writeBoolean(itemTemplate.isUpToUp());
            }
            this.dataItemTemplate = message.getData();
        } catch (Exception e) {
            LogServer.LogException("Error sending item template: " + e.getMessage());
        }
    }

    private void setItemOption() {
        try (Message message = new Message()) {
            message.writer().writeByte(ITEM_OPTION);
            message.writer().writeByte(0); //update option
            message.writer().writeShort(itemOptionTemplates.size());// dis true
            for (ItemOptionTemplate itemOptionTemplate : itemOptionTemplates.values()) {
                message.writer().writeUTF(itemOptionTemplate.name());
                message.writer().writeByte(itemOptionTemplate.type());
            }
            this.dataItemOption = message.getData();
        } catch (Exception e) {
            LogServer.LogException("Error sending skill template: " + e.getMessage());
        }
    }

    private void setItemArrHead2fr() {
        try (Message message = new Message()) {
            message.writer().writeByte(ITEM_ARR_HEAD_2FR);
            message.writer().writeShort(arrHead2Frames.size());
            for (ItemTemplate.ArrHead2Frames arrHead2Frames : arrHead2Frames) {
                message.writer().writeByte(arrHead2Frames.frames().size());
                for (Integer head : arrHead2Frames.frames()) {
                    message.writer().writeShort(head);
                }
            }
            this.dataArrHead2Fr = message.getData();
        } catch (Exception e) {
            LogServer.LogException("Error sending item arr head 2 fr: " + e.getMessage());
        }
    }

    public void logItemTemplate() {
        if (itemTemplates.isEmpty()) {
            throw new RuntimeException("ItemTemplates is empty");
        }
        for (ItemTemplate itemTemplate : this.itemTemplates.values()) {
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
