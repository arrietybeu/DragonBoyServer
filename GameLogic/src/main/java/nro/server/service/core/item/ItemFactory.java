package nro.server.service.core.item;

import lombok.Getter;
import nro.server.service.model.item.Item;
import nro.server.service.model.template.item.ItemOption;
import nro.server.service.model.template.item.ItemTemplate;
import nro.server.manager.ItemManager;

import java.util.ArrayList;
import java.util.List;

public class ItemFactory {

    @Getter
    private final static ItemFactory instance = new ItemFactory();

    private final static ItemManager itemManager = ItemManager.getInstance();

    public Item createItemNotOptionsBase(int tempId, int... quantitys) {
        var quantity = (quantitys.length > 0 && quantitys[0] > 0) ? quantitys[0] : 1;
        Item item = new Item();
        item.setTemplate(getTemplate(tempId));
        item.setQuantity(quantity);
        item.setCreateTime(System.currentTimeMillis());
        return item;
    }

    public Item createItemOptionsBase(int itemId, int... quantitys) {
        var quantity = (quantitys.length > 0 && quantitys[0] > 0) ? quantitys[0] : 1;
        Item item = this.createItemNotOptionsBase(itemId, quantity);
        this.initBaseOptions(item);
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

    public List<Item> initializePlayerItems(byte clazz) throws RuntimeException {
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
            Item item = createItemOptionsBase(itemId);
            if (item != null) {
                items.add(item);
            } else {
                throw new RuntimeException("Failed to create item id: " + itemId);
            }
        }
        return items;
    }

    public List<Item> initItemBox() {
        List<Item> items = new ArrayList<>();

        var itemId = 12;
        Item item = createItemOptionsBase(itemId);
        if (item != null) {
            items.add(item);
        } else {
            throw new RuntimeException("Failed to create item id: " + itemId);
        }
        return items;
    }

    public static boolean isItemPea(int itemId) {
        ItemManager itemManager = ItemManager.getInstance();
        ItemTemplate item = itemManager.getItemTemplates().get((short) itemId);
        return item != null && item.type() == 6;
    }

}
