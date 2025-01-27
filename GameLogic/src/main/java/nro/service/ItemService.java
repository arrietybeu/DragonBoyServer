package nro.service;

import lombok.Getter;
import nro.model.item.Item;
import nro.model.item.ItemTemplate;
import nro.server.manager.ItemManager;

public class ItemService {

    @Getter
    private final static ItemService instance = new ItemService();

    public Item createItem(short tempId, int quantity) {
        Item item = new Item();
        item.setTemplate(getTemplate(tempId));
        item.setQuantity(quantity);
        item.setCreateTime(System.currentTimeMillis());
        item.setContent(item.getContent());
        item.setInfo(item.getInfo());
        return item;
    }

    public ItemTemplate getTemplate(short id) {
        return ItemManager.getInstance().getItemTemplates().get(id);
    }

}
