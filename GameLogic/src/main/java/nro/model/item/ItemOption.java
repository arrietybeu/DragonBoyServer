package nro.model.item;

import lombok.Data;
import nro.server.manager.ItemManager;

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

    public static ItemOption getItemOptionBase(short idItem) {
        ItemTemplate base = itemManager.getItemTemplates().get(idItem);
        for (ItemOption option : base.options()) {
            System.out.println(option.toString() + " " + getNameOption(option.id));
            return option;
        }
        return null;
    }

    public static String getNameOption(int id) {
        return itemManager.getItemOptionTemplates().get((short) id).name();
    }
}
