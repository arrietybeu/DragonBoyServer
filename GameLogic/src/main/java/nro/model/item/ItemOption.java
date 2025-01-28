package nro.model.item;

import lombok.Data;
import nro.server.manager.ItemManager;
import org.json.simple.JSONArray;

@Data
public class ItemOption {

    private final static ItemManager itemManager = ItemManager.getInstance();

    public ItemOptionTemplate optionTemplate;
    public int id;
    public int param;

    public ItemOption(ItemOption io) {
        this.param = io.param;
        this.optionTemplate = io.optionTemplate;
    }

    // constructor item options base
    public ItemOption(int id, int param) {
        this.id = id;
        this.param = param;
    }

    // get option string

}
