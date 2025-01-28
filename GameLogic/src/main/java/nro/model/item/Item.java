package nro.model.item;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@SuppressWarnings("unchecked")
public class Item {
    private int quantity;
    private long createTime;
    private ItemTemplate template;
    private List<ItemOption> itemOptions = new ArrayList<>();
    private String info;
    private String content;

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
}
