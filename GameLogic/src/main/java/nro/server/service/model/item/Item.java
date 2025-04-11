package nro.server.service.model.item;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nro.consts.ConstItem;
import nro.server.service.model.template.item.ItemOption;
import nro.server.service.model.template.item.ItemTemplate;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@SuppressWarnings("ALL")
public class Item implements AutoCloseable {

    private int creatorPlayerId = -1;
    private int quantity;
    private long createTime;
    private ItemTemplate template;
    private List<ItemOption> itemOptions;

    public Item() {
        this.itemOptions = new ArrayList<>();
        this.createTime = System.currentTimeMillis();
    }

    public void addOption(int id, int param) {
        this.itemOptions.add(new ItemOption(id, param));
    }

    public String getJsonOptions() {
        JSONArray options = new JSONArray();
        for (ItemOption io : this.itemOptions) {
            JSONArray option = new JSONArray();
            if (io == null)
                continue;
            if (io.getId() == 73)
                continue;
            option.add(io.getId());
            option.add(io.getParam());
            options.add(option);
        }
        return options.toJSONString();
    }

    public void setJsonOptions(String jsonOptions) {
        if (jsonOptions == null || jsonOptions.isEmpty())
            return;
        try {
            JSONArray jsonArray = (JSONArray) new JSONParser().parse(jsonOptions);
            for (Object obj : jsonArray) {
                if (!(obj instanceof JSONArray optionArray))
                    continue;

                if (optionArray.size() == 2) {
                    Number optionId = (Number) optionArray.get(0);
                    Number optionValue = (Number) optionArray.get(1);
                    this.itemOptions.add(new ItemOption(optionId.intValue(), optionValue.intValue()));
                }
            }
        } catch (ParseException e) {
            System.err.println("Lỗi parse JSON options: " + jsonOptions);
            e.printStackTrace();
        }
    }

    public void subQuantity(int quantity) {
        this.quantity -= quantity;
        if (this.quantity < 0) {
            this.quantity = 0;
        }
    }

    public void writeDataOptions(DataOutputStream dataOutputStream) throws IOException {
        if (itemOptions.isEmpty()) {
            dataOutputStream.writeByte(1);
            dataOutputStream.writeShort(73);
            dataOutputStream.writeInt(0);
            return;
        }
        dataOutputStream.writeByte(itemOptions.size());
        for (ItemOption option : itemOptions) {
            dataOutputStream.writeShort(option.getId());
            dataOutputStream.writeInt(option.getParam());
        }
    }

    public boolean displayDisguise() {
        return (this.template.type() == 0 && this.template.body() != -1)
                || (this.template.type() == 1 && this.template.leg() != -1)
                || this.template.type() == 5
                || (this.template.type() == 11 && this.template.part() != -1);
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public boolean isItemMount() {
        return this.template.type() == ConstItem.TYPE_MOUNT || this.template.type() == ConstItem.TYPE_MOUNT_VIP;
    }

    public void dispose() {
        this.quantity = 0;
        this.template = null;
        this.itemOptions.clear();
    }

    @Override
    public void close() {
        this.dispose();
    }
}
