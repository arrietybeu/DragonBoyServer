package nro.model.item;

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuppressWarnings("ALL")
public class Item implements AutoCloseable {

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

    @SuppressWarnings("unchecked")
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

    public void addQuantity(int quantity) {
        // System.out.println("addQuantity: " + quantity);
        this.quantity += quantity;
    }

    public void dispose() {
        this.quantity = 0;
        this.createTime = 0;
        this.template = null;
        this.itemOptions.clear();
    }

    @Override
    public void close() {
        this.dispose();
    }
}
