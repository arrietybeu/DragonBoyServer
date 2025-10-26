package nro.server.data_holders.repo;

import lombok.Getter;
import nro.commons.database.Database;
import nro.commons.utils.NetworkUtils;
import nro.server.configs.main.ConfigServer;
import nro.server.data_holders.GameEngine;
import nro.server.model.item.ItemOptionData;
import nro.server.model.templates.item.ItemOptionTemplate;
import nro.server.model.templates.item.ItemTemplate;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Arriety
 */
@Getter
public final class ItemData implements GameEngine {

    private static final Logger log = LoggerFactory.getLogger(ItemData.class);

    private final Map<Short, ItemOptionTemplate> itemOptionTemplates = new HashMap<>();
    private byte[] dataItemOption;

    private final Map<Short, ItemTemplate> itemTemplates = new HashMap<>();
    private byte[] dataItemTemplate;

    private final List<ItemTemplate.ArrHead2Frames> arrHead2Frames = new ArrayList<>();
    private byte[] dataArrHead2Fr;

    private final List<ItemTemplate.HeadAvatar> itemHeadAvatars = new ArrayList<>();
    private byte[] dataItemHead;

    @Override
    public void init() throws Throwable {
        Database.withConnection(connection -> {
            loadItemOptionTemplate(connection);
            loadItemTemplate(connection);
            loadItemArrHead2Frame(connection);
            loadItemHead(connection);
            return null;
        });
    }

    @Override
    public void reload() throws Throwable {
    }

    @Override
    public void clear() throws Throwable {
        this.itemOptionTemplates.clear();
        this.dataItemOption = null;
    }

    private void loadItemHead(Connection connection) {
        String query = "SELECT * FROM item_head";
        Database.select(connection, query, rs -> {
            while (rs.next()) {
                int headId = rs.getInt("head_id");
                int avatarId = rs.getInt("avatar_id");
                ItemTemplate.HeadAvatar headAvatar = new ItemTemplate.HeadAvatar(headId, avatarId);
                itemHeadAvatars.add(headAvatar);
            }
        });

        setItemHead();
    }

    private void setItemHead() {
        ByteBuffer buf = ByteBuffer.allocate(100_000);

        buf.putShort((short) itemHeadAvatars.size());
        for (var head : itemHeadAvatars) {
            buf.putShort((short) head.headId());
            buf.putShort((short) head.avatarId());
        }

        buf.flip();
        this.dataItemHead = new byte[buf.remaining()];
        buf.get(this.dataItemHead);
    }

    private void loadItemOptionTemplate(Connection con) {
        String query = "SELECT * FROM item_option_template";
        Database.select(con, query, rs -> {
            while (rs.next()) {
                var id = rs.getInt("id");
                var name = rs.getString("name");
                var type = rs.getByte("type");
                var status = rs.getByte("status");
                ItemOptionTemplate itemOptionManager = new ItemOptionTemplate(id, name, type, status);
                if (id > Byte.MAX_VALUE) {
//                    log.warn("Item option bo qua id: {}, name {} vi qua gioi han byte", id, name);
                    continue;
                }
                this.itemOptionTemplates.put((short) id, itemOptionManager);
            }
        });

        this.setItemOption();
    }

    private void setItemOption() {
        ByteBuffer buf = ByteBuffer.allocate(100_000);

        buf.put((byte) 0);// type send option
        buf.putShort((short) itemOptionTemplates.size()); // size option
        for (var option : itemOptionTemplates.values()) {
            NetworkUtils.writeString(buf, option.name());
            buf.put(option.type());
        }

        buf.flip();
        this.dataItemOption = new byte[buf.remaining()];
        buf.get(this.dataItemOption);
    }

    private void loadItemTemplate(Connection con) {
        String sql = "SELECT * FROM `item_template`";
        Database.select(con, sql, resultSet -> {
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

                List<ItemOptionData> itemOptionData = new ArrayList<>();
                JSONArray dataArray = (JSONArray) JSONValue.parse(options);
                if (dataArray == null) {
                    throw new RuntimeException("Error load options item id: " + id);
                }
                for (Object o : dataArray) {
                    JSONArray opt = (JSONArray) o;
                    var idOption = Short.parseShort(String.valueOf(opt.get(0)));
                    var param = Integer.parseInt(String.valueOf(opt.get(1)));
                    itemOptionData.add(new ItemOptionData(idOption, param, (short) 0));
                }

                var itemTemplate = new ItemTemplate(id, type, gender, name, description, level, iconID, part, maxQuantity, powerRequire, head, body, leg, itemOptionData, isTrade);
                this.itemTemplates.put(id, itemTemplate);
            }
        });
        this.setDataItemTemplate();
    }

    private void setDataItemTemplate() {
        ByteBuffer buf = ByteBuffer.allocate(1_500_000);
        buf.put((byte) 0);
        buf.put(ConfigServer.VERSION_DATA_ITEM);
        buf.put((byte) 1);
        buf.putShort((short) itemTemplates.size());
        for (short i = 0; i < itemTemplates.size(); i++) {
            var item = this.itemTemplates.get(i);
            if (item == null) {
                throw new IllegalArgumentException("Item not found for index: " + i);
            }
            buf.put(item.type());
            buf.put(item.gender());
            NetworkUtils.writeString(buf, item.name());
            NetworkUtils.writeString(buf, item.description());
            buf.put(item.level());
            buf.putInt(item.strRequire());
            buf.putShort(item.iconID());
            buf.putShort(item.part());
            buf.put((byte) 0);
        }
        buf.flip();
        this.dataItemTemplate = new byte[buf.remaining()];
        buf.get(this.dataItemTemplate);
    }

    private void loadItemArrHead2Frame(Connection con) {
        String sql = "SELECT id, head_one, head_two FROM `item_arr_head_2frame`";
        Database.select(con, sql, resultSet -> {
            while (resultSet.next()) {
                var id = resultSet.getInt("id");
                var head_one = resultSet.getInt("head_one");
                var head_two = resultSet.getInt("head_two");

                List<Integer> heads = new ArrayList<>();
                heads.add(head_one);
                heads.add(head_two);

                this.arrHead2Frames.add(new ItemTemplate.ArrHead2Frames(id, heads));
            }
        });
        this.setItemArrHead2fr();
    }

    private void setItemArrHead2fr() {
        ByteBuffer buf = ByteBuffer.allocate(100_000);
        buf.put((byte) 100);
        buf.putShort((short) arrHead2Frames.size());
        for (var arrHead2Frame : arrHead2Frames) {
            buf.put((byte) arrHead2Frame.frames().size());
            for (Integer i : arrHead2Frame.frames()) {
                buf.putShort(i.shortValue());
            }
        }
        buf.flip();
        this.dataArrHead2Fr = new byte[buf.remaining()];
        buf.get(this.dataArrHead2Fr);
    }

    public byte findTypeItemOption(int id) {
        ItemOptionTemplate itemOptionTemplate = itemOptionTemplates.get((short) id);
        if (itemOptionTemplate == null) return -1;
        return itemOptionTemplate.type();
    }

    public static ItemData getInstance() {
        return ItemDataHolder.INSTANCE;
    }

    private static class ItemDataHolder {
        private static final ItemData INSTANCE = new ItemData();
    }
}