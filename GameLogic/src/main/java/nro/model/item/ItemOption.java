package nro.model.item;

import lombok.Data;
import nro.server.manager.ItemManager;
import nro.utils.Util;

import java.util.HashMap;
import java.util.Map;

@Data
public class ItemOption {

    private final static ItemManager itemManager = ItemManager.getInstance();

    public int id;
    public int param;

    public ItemOption(int id, int param) {
        this.id = id;
        this.param = param;
    }

    private static Map<String, String> OPTION_STRING = new HashMap<>();

    public String getOptionString(int idOptions) {
        ItemOptionTemplate optionTemplate = itemManager.getItemOptionTemplates().get((short) idOptions);
        String key = optionTemplate.name() + "#" + this.param + "#";
        String value = OPTION_STRING.get(key);
        if (value == null) {
            value = this.replace(optionTemplate.name(), "#", String.valueOf(this.param));
            OPTION_STRING.put(key, value);
        }
        return value;
    }

    public String replace(String text, String regex, String replacement) {
        return text.replace(regex, replacement);
    }
}
