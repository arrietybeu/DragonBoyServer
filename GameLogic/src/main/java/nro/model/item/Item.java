package nro.model.item;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nro.utils.Util;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@SuppressWarnings("unchecked")
public class Item {

    private int quantity;
    private long createTime;
    private ItemTemplate template;
    private final List<ItemOption> itemOptions;

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
            option.add(io.id);
            option.add(io.param);
            options.add(option);
        }
        return options.toJSONString();
    }

    public void setJsonOptions(String jsonOptions) {
        if (jsonOptions == null || jsonOptions.isEmpty()) return;
        try {
            JSONArray jsonArray = (JSONArray) new JSONParser().parse(jsonOptions);
            for (Object obj : jsonArray) {
                if (!(obj instanceof JSONArray)) continue;
                JSONArray optionArray = (JSONArray) obj;
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

    public void addQuantity(int quantity) {
        System.out.println("addQuantity: " + quantity);
        this.quantity += quantity;
    }

    public void dispose() {
        this.quantity = 0;
        this.createTime = 0;
        this.template = null;
        this.itemOptions.clear();
    }

}
