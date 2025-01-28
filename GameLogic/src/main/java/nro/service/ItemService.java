package nro.service;

import lombok.Getter;
import nro.model.item.Item;
import nro.model.item.ItemOption;
import nro.model.item.ItemTemplate;
import nro.server.manager.ItemManager;

public class ItemService {

    @Getter
    private final static ItemService instance = new ItemService();

    private final static ItemManager itemManager = ItemManager.getInstance();

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
        return itemManager.getItemTemplates().get(id);
    }

    public void initBaseOptions(Item item) throws RuntimeException {
        item.getItemOptions().clear();
        for (int i = 0; i < item.getTemplate().options().size(); i++) {
            item.getItemOptions().add(ItemOption.getItemOptionBase(item.getTemplate().id()));
        }
    }
}
