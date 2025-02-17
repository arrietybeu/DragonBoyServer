package nro.model.item;

import lombok.Data;
import nro.server.manager.ItemManager;

@Data
public class ItemOption {

    private final static ItemManager itemManager = ItemManager.getInstance();

    public int id;
    public int param;

    public ItemOption(int id, int param) {
        this.id = id;
        this.param = param;
    }

    public ItemOption() {
        this.id = 73;
        this.param = 0;
    }

    public String getOptionString(int idOptions) {
        ItemOptionTemplate optionTemplate = itemManager.getItemOptionTemplates().get((short) idOptions);
        return optionTemplate.name().replace("#", String.valueOf(param));
    }

}
