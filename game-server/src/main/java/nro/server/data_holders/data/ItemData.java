package nro.server.data_holders.data;

import lombok.Getter;
import nro.commons.configs.DatabaseConfig;
import nro.commons.database.Database;
import nro.commons.database.DatabaseFactory;
import nro.server.data_holders.IManager;
import nro.server.model.item.ItemOption;
import nro.server.model.templates.item.ItemOptionTemplate;
import nro.server.model.templates.item.ItemTemplate;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Arriety
 */
@Getter
public final class ItemData implements IManager {

    private static final Logger log = LoggerFactory.getLogger(ItemData.class);


    private final Map<Short, ItemOptionTemplate> itemOptionTemplates = new HashMap<>();
    private byte[] dataItemOption;

    private final Map<Short, ItemTemplate> itemTemplates = new HashMap<>();
    private byte[] dataItemTemplate;
    private byte[] dataItemTemplate2;

    private final List<ItemTemplate.ArrHead2Frames> arrHead2Frames = new ArrayList<>();
    private byte[] dataArrHead2Fr;

    @Override
    public void init() throws Throwable {
        loadItemOptionTemplate();
        loadItemTemplate();
        loadItemArrHead2Frame();
    }

    @Override
    public void reload() throws Throwable {
    }

    @Override
    public void clear() throws Throwable {
        this.itemOptionTemplates.clear();
        this.dataItemOption = null;
    }


    private void loadItemOptionTemplate() {
        String query = "SELECT * FROM item_option_template";
        Database.select(query, rs -> {
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
            this.setItemOption();
        });
    }

    private void setItemOption() {
        ByteBuffer buf = ByteBuffer.allocate(100_000);

        buf.put((byte) 0);// type send option
        buf.put((byte) itemOptionTemplates.size()); // size option
        for (var option : itemOptionTemplates.values()) {
            this.putString(buf, option.name());
            buf.put(option.type());
        }

        buf.flip();
        this.dataItemOption = new byte[buf.remaining()];
        buf.get(this.dataItemOption);
    }

    private void loadItemTemplate() {
        String sql = "SELECT * FROM `item_template`";
        Database.select(sql, resultSet -> {
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
                    itemOptions.add(new ItemOption(idOption, param, (short) 0));
                }

                var itemTemplate = new ItemTemplate(id, type, gender, name, description, level, iconID, part, maxQuantity, powerRequire, head, body, leg, itemOptions, isTrade);
                this.itemTemplates.put(id, itemTemplate);
            }
            this.setDataItemTemplate();
        });
    }

    private void setDataItemTemplate() {
        short count = 800;
        ByteBuffer buf = ByteBuffer.allocate(100_000);
        buf.put((byte) 1); // type send item template
        buf.putShort(count); // size item template
        for (int i = 0; i < 800; i++) {
            this.setDataItem(buf, i);
        }
        buf.flip();
        this.dataItemTemplate = new byte[buf.remaining()];
        buf.get(this.dataItemTemplate);

        ByteBuffer buf2 = ByteBuffer.allocate(100_000);
        buf2.put((byte) 2); // type send item template 2
        buf2.putShort(count);
        buf2.putShort((short) itemOptionTemplates.size());
        for (int i = count; i < itemOptionTemplates.size(); i++) {
            this.setDataItem(buf2, i);
        }
        buf2.flip();
        this.dataItemTemplate2 = new byte[buf2.remaining()];
        buf2.get(this.dataItemTemplate2);
    }

    private void setDataItem(ByteBuffer buf, int index) {
        var item = itemTemplates.get((short) index);
        if (item == null) {
            throw new IllegalArgumentException("Item not found for index: " + index);
        }
        buf.put(item.type());
        buf.put(item.gender());
        this.putString(buf, item.name());
        this.putString(buf, item.description());
        buf.put(item.level());
        buf.putShort(item.iconID());
        buf.putShort(item.part());
        buf.put((byte) 0);
    }

    private void loadItemArrHead2Frame() {
        String sql = "SELECT id, head_one, head_two FROM `item_arr_head_2frame`";
        Database.select(sql, resultSet -> {
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

    private void putString(ByteBuffer buf, String str) {
        if (str == null) str = "";
        byte[] data = str.getBytes(StandardCharsets.UTF_8);
        if (data.length > Short.MAX_VALUE) throw new IllegalArgumentException("String quá dài: " + data.length);
        buf.putShort((short) data.length);
        buf.put(data);
    }

    public static ItemData getInstance() {
        return ItemDataHolder.INSTANCE;
    }

    private static class ItemDataHolder {
        private static final ItemData INSTANCE = new ItemData();
    }
}