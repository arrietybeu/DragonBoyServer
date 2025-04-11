package nro.server.manager;

import lombok.Getter;
import nro.consts.ConstItem;
import nro.server.network.Message;
import nro.server.config.ConfigDB;
import nro.server.service.model.template.item.*;
import nro.server.service.repositories.DatabaseFactory;
import nro.server.config.ConfigServer;
import nro.server.system.LogServer;
import nro.server.service.core.item.ItemFactory;
import nro.server.service.model.item.Item;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public final class ItemManager implements IManager {

    @Getter
    private static final ItemManager instance = new ItemManager();

    private static final byte ITEM_OPTION = 0;
    private static final byte ITEM_NORMAL = 1;
    private static final byte ITEM_ARR_HEAD_2FR = 100;

    private final Map<Short, ItemTemplate> itemTemplates = new HashMap<>();
    private final Map<Short, ItemOptionTemplate> itemOptionTemplates = new HashMap<>();

    private final List<ItemTemplate.HeadAvatar> itemHeadAvatars = new ArrayList<>();
    private final List<ItemTemplate.ArrHead2Frames> arrHead2Frames = new ArrayList<>();
    private final List<Flag> flags = new ArrayList<>();
    private final List<FlagImage> flagImages = new ArrayList<>();

    private byte[] dataItemTemplate;
    private byte[] dataItemOption;
    private byte[] dataArrHead2Fr;
    private byte[] dataItemhead;

    @Override
    public void init() {
        this.loadItemTemplate();
        this.loadItemArrHead2Fr();
        this.loadItemOptionTemplate();
        this.loadHeadAvatar();
        this.loadFlagBag();
        this.loadFlagBagImage();
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
        this.itemHeadAvatars.clear();
        this.flags.clear();
        this.flagImages.clear();
        this.dataItemTemplate = null;
        this.dataItemOption = null;
        this.dataArrHead2Fr = null;
        this.dataItemhead = null;
    }

    private void loadItemTemplate() {
        String sql = "SELECT * FROM `item_template`";
        try (Connection connection = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_STATIC)) {
            assert connection != null : "Connection is null";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql); var resultSet = preparedStatement.executeQuery()) {
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
                    var maxQuantity = resultSet.getInt("max_quantity");
                    var head = resultSet.getShort("head");
                    var body = resultSet.getShort("body");
                    var leg = resultSet.getShort("leg");
                    var options = resultSet.getString("options");
                    boolean isTrade = resultSet.getByte("is_trade") == 1;

                    List<ItemOption> itemOptions = new ArrayList<>();
                    JSONArray dataArray = (JSONArray) JSONValue.parse(options);
                    if (dataArray == null) {
                        throw new RuntimeException("Error load options item id: " + id);
                    }
                    for (Object o : dataArray) {
                        JSONArray opt = (JSONArray) o;
                        var idOption = Short.parseShort(String.valueOf(opt.get(0)));
                        var param = Integer.parseInt(String.valueOf(opt.get(1)));
                        itemOptions.add(new ItemOption(idOption, param));
                    }

                    var itemTemplate = new ItemTemplate(id, type, gender, name, description, level, iconID,
                            part, maxQuantity, powerRequire, head, body, leg, itemOptions, isTrade);
                    this.itemTemplates.put(id, itemTemplate);
                }
                this.setDataItemTemplate();
                // LogServer.LogInit("ItemManager initialized size: " + itemTemplates.size());
            }
        } catch (Exception e) {
            LogServer.LogException("Error loadItem: " + e.getMessage(), e);
        }
    }

    private void loadItemOptionTemplate() {
        String query = "SELECT * FROM item_option_template";

        try (Connection connection = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_STATIC); PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                var id = rs.getInt("id");
                var name = rs.getString("name");
                var type = rs.getByte("type");
                var status = rs.getByte("status");
                ItemOptionTemplate itemOptionManager = new ItemOptionTemplate(id, name, type, status);
                this.itemOptionTemplates.put((short) id, itemOptionManager);
            }
            this.setItemOption();
            // LogServer.LogInit("ItemOptionManager initialized size: " +
            // this.itemOptionTemplates.size());
        } catch (Exception e) {
            LogServer.LogException("Error loadItemOptionTemplate: " + e.getMessage(), e);
        }
    }

    private void loadHeadAvatar() {
        String sql = "SELECT * FROM  item_head";
        try (var connection = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_STATIC)) {
            if (connection == null) throw new SQLException("Connect connection select item_head = null");
            try (var preparedStatement = connection.prepareStatement(sql); var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int headId = resultSet.getInt("head_id");
                    int avatarId = resultSet.getInt("avatar_id");
                    ItemTemplate.HeadAvatar headAvatar = new ItemTemplate.HeadAvatar(headId, avatarId);
                    itemHeadAvatars.add(headAvatar);
                }
            }
            this.setDataItemHead();
        } catch (SQLException e) {
            LogServer.LogException("Error loadHeadAvatar: " + e.getMessage(), e);
        }
        // LogServer.LogInit("Item loadHeadAvatar initialized size: " +
        // itemHeadAvatars.size());
    }

    private void loadFlagBag() {
        String query = "SELECT * FROM item_flag_bag_pk";
        try (var connection = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_STATIC)) {
            if (connection == null) throw new SQLException("Connect connection select flag_bag = null");
            try (var preparedStatement = connection.prepareStatement(query); var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int itemId = resultSet.getInt("item_id");
                    int icon = resultSet.getInt("icon");

                    Item itemFlagBag = ItemFactory.getInstance().createItemOptionsBase(itemId, ConstItem.FLAG_BAG, 1);
                    Flag flag = new Flag(id, itemId, icon, itemFlagBag);
                    this.flags.add(flag);
                }
            }
        } catch (SQLException e) {
            LogServer.LogException("Error loadFlagBag: " + e.getMessage(), e);
        }
        // LogServer.LogInit("Item Flag initialized size: " + flags.size());
    }

    private void loadFlagBagImage() {
        String query = "SELECT * FROM item_flag_bag_image";
        try (var connection = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_STATIC)) {
            if (connection == null) throw new SQLException("Connect connection select flag_bag_image = null");
            try (var preparedStatement = connection.prepareStatement(query); var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String jsonEffect = resultSet.getString("effect");
                    short icon = resultSet.getShort("icon");
                    JSONArray jsonArray = (JSONArray) JSONValue.parse(jsonEffect);
                    if (jsonArray == null) {
                        throw new RuntimeException("Error load effect item id: " + id);
                    }
                    short[] iconEffect = new short[jsonArray.size()];
                    for (int i = 0; i < jsonArray.size(); i++) {
                        iconEffect[i] = Short.parseShort(String.valueOf(jsonArray.get(i)));
                    }
                    FlagImage flagImage = new FlagImage(id, name, icon, iconEffect);
                    this.flagImages.add(flagImage);
                }
            }
        } catch (SQLException e) {
            LogServer.LogException("Error loadFlagBagImage: " + e.getMessage(), e);
        }
    }

    private void loadItemArrHead2Fr() {
        String sql = "SELECT id, head_one, head_two FROM `item_arr_head_2frame`";
        try (var connection = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_STATIC)) {
            assert connection != null : "Connection is null";
            try (var preparedStatement = connection.prepareStatement(sql); var resultSet = preparedStatement.executeQuery()) {
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
            this.setItemArrHead2fr();
        } catch (Exception e) {
            LogServer.LogException("Error loadItemArr_Head_2Fr: " + e.getMessage(), e);
        }
        // LogServer.LogInit("Item ArrHead2Frames initialized size: " +
        // arrHead2Frames.size());
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
                // message.writer().writeUTF(itemTemplate.name());
                message.writer().writeUTF("[ID: " + itemTemplate.id() + "] " + itemTemplate.name());// test
                message.writer().writeUTF(itemTemplate.description());
                message.writer().writeByte(itemTemplate.level());
                message.writer().writeInt(itemTemplate.strRequire());
                message.writer().writeShort(itemTemplate.iconID());
                message.writer().writeShort(itemTemplate.part());
                // message.writer().writeBoolean(itemTemplate.isUpToUp());
                message.writer().writeBoolean(false);// is up to up
            }
            this.dataItemTemplate = message.getData();
        } catch (Exception e) {
            LogServer.LogException("Error sending item template: " + e.getMessage(), e);
        }
    }

    private void setItemOption() {
        try (Message message = new Message()) {
            message.writer().writeByte(ITEM_OPTION); // update option
            message.writer().writeShort(itemOptionTemplates.size());// dis true
            for (ItemOptionTemplate itemOptionTemplate : itemOptionTemplates.values()) {
                message.writer().writeUTF("id: " + itemOptionTemplate.id() + "/" + itemOptionTemplate.name());
//                message.writer().writeUTF(itemOptionTemplate.name());
                message.writer().writeByte(itemOptionTemplate.type());
            }
            this.dataItemOption = message.getData();
        } catch (Exception e) {
            LogServer.LogException("Error sending skill template: " + e.getMessage(), e);
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
            LogServer.LogException("Error sending item arr head 2 fr: " + e.getMessage(), e);
        }
    }

    private void setDataItemHead() {
        try (Message message = new Message()) {
            message.writer().writeShort(itemHeadAvatars.size());
            for (var headAvatar : itemHeadAvatars) {
                message.writer().writeShort(headAvatar.headId());
                message.writer().writeShort(headAvatar.avatarId());
            }
            this.dataItemhead = message.getData();
        } catch (Exception e) {
            LogServer.LogException("Error sending item setDataItemHead: " + e.getMessage(), e);
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

    public Flag findFlagId(int id) {
        for (Flag flag : flags) {
            if (flag.id() == id) {
                return flag;
            }
        }
        return null;
    }

    public FlagImage findFlagImageId(int id) {
        for (FlagImage flagImage : flagImages) {
            if (flagImage.getId() == id) {
                return flagImage;
            }
        }
        return null;
    }

    public byte findTypeItemOption(int id) {
        ItemOptionTemplate itemOptionTemplate = itemOptionTemplates.get((short) id);
        if (itemOptionTemplate == null) return -1;
        return itemOptionTemplate.type();
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
