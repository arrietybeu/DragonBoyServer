package nro.service;

import lombok.Getter;
import nro.model.item.Item;
import nro.model.item.ItemOption;
import nro.model.item.ItemTemplate;
import nro.server.manager.ItemManager;

import java.util.ArrayList;
import java.util.List;

public class ItemService {

    @Getter
    private final static ItemService instance = new ItemService();

    private final static ItemManager itemManager = ItemManager.getInstance();

    public Item createItem(int tempId, int quantity) {
        Item item = new Item();
        item.setTemplate(getTemplate(tempId));
        System.out.println("Item created: " + item.getTemplate().name());
        item.setQuantity(quantity);
        item.setCreateTime(System.currentTimeMillis());
        return item;
    }

    public Item createItemNull() {
        return new Item();
    }

    public Item clone(Item item) {
        Item itemClone = new Item();
        itemClone.setTemplate(item.getTemplate());
        itemClone.setQuantity(item.getQuantity());
        for (ItemOption io : item.getItemOptions()) {
            itemClone.getItemOptions().add(io);
        }
        return itemClone;
    }

    public ItemTemplate getTemplate(int id) {
        return itemManager.getItemTemplates().get((short) id);
    }

    public void initBaseOptions(Item item) throws RuntimeException {
        item.getItemOptions().clear();
        if (item.getTemplate().options().isEmpty()) {
            ItemOption option = new ItemOption();
            item.getItemOptions().add(option);
        } else {
            for (ItemOption option : item.getTemplate().options()) {
                item.getItemOptions().add(option);
            }
        }
    }

    public static List<Item> initializePlayerItems(byte clazz) throws RuntimeException {
        List<Item> items = new ArrayList<>();

        // class[0] = trai dat,
        // class[1] = namec,
        // class[2] = xayda,

        short[][] itemIdsByClass = {
                {0, 6},
                {1, 7},
                {2, 8}
        };

        if (clazz < 0 || clazz > itemIdsByClass.length) {
            return items;
        }

        short[] itemIds = itemIdsByClass[clazz];

        for (short itemId : itemIds) {
            Item item = createAndInitItem(itemId);
            if (item != null) {
                items.add(item);
            } else {
                throw new RuntimeException("Failed to create item id: " + itemId);
            }
        }
        return items;
    }

    public static List<Item> initItemBox() {
        List<Item> items = new ArrayList<>();

        var itemId = 12;
        Item item = createAndInitItem(itemId);
        if (item != null) {
            System.out.println("Item created: " + item.getTemplate().name());
            items.add(item);
        } else {
            throw new RuntimeException("Failed to create item id: " + itemId);
        }
        return items;
    }

    public static Item createAndInitItem(int itemId) {
        Item item = ItemService.getInstance().createItem(itemId, 1);
        ItemService.getInstance().initBaseOptions(item);
        return item;
    }

}
