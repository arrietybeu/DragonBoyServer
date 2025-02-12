package nro.model.item;

import lombok.Data;
import lombok.NoArgsConstructor;
import nro.utils.Util;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@SuppressWarnings("unchecked")
public class Item {
    private int quantity;
    private long createTime;
    private ItemTemplate template;
    private final List<ItemOption> itemOptions;
    private String info;
    private String content;

    public Item() {
        this.itemOptions = new ArrayList<>();
        this.createTime = System.currentTimeMillis();
    }

    public String getContent() {
        return "Yêu cầu sức mạnh " + this.template.strRequire() + " trở lên";
    }

    public String getInfoOption() {
        String strInfo = "";
        for (ItemOption itemOption : this.itemOptions) {
            strInfo += itemOption.getOptionString(itemOption.getId());
        }
        return strInfo;
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

}
