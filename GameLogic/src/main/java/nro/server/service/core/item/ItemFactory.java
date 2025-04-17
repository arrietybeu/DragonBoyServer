package nro.server.service.core.item;

import nro.consts.ConstItem;
import nro.server.service.model.item.Item;
import nro.server.service.model.item.ItemShop;
import nro.server.service.model.template.item.ItemOption;
import nro.server.service.model.template.item.ItemTemplate;
import nro.server.manager.ItemManager;
import nro.server.system.LogServer;

import java.util.ArrayList;
import java.util.List;

public class ItemFactory {

    private static final class SingletonHolder {
        private static final ItemFactory instance = new ItemFactory();
    }

    public static ItemFactory getInstance() {
        return ItemFactory.SingletonHolder.instance;
    }

    private final static ItemManager itemManager = ItemManager.getInstance();

    public Item createItemNotOptionsBase(int tempId, int creatorId, int quantity) {
        if (quantity <= 0) {
            quantity = 1;
        }
        Item item = new Item();
        item.setCreatorPlayerId(creatorId);
        item.setTemplate(getTemplate(tempId));
        item.setQuantity(quantity);
        item.setCreateTime(System.currentTimeMillis());
        return item;
    }

    public Item createItemOptionsBase(int itemId, int creatorId, int quantity) {
        Item item = this.createItemNotOptionsBase(itemId, creatorId, quantity);
        this.initBaseOptions(item);
        return item;
    }

    public ItemShop createItemShopOptionsBase(int itemId, int... quantitys) {
        int quantity = (quantitys.length > 0 && quantitys[0] > 0) ? quantitys[0] : 1;
        ItemShop itemShop = new ItemShop();
        itemShop.setTemplate(getTemplate(itemId));
        itemShop.setQuantity(quantity);
        this.initBaseOptions(itemShop);
        return itemShop;
    }

    public Item createItemNull() {
        return new Item();
    }

    public Item clone(Item original) {

        if (original == null || original.getTemplate() == null) {
            return null;
        }

        Item clone = new Item();
        clone.setTemplate(original.getTemplate());
        clone.setQuantity(original.getQuantity());
        clone.setCreateTime(original.getCreateTime());
        clone.setCreatorPlayerId(original.getCreatorPlayerId());

        List<ItemOption> optionClones = new ArrayList<>();
        for (ItemOption option : original.getItemOptions()) {
            ItemOption optionClone = new ItemOption();
            optionClone.setId(option.getId());
            optionClone.setParam(option.getParam());
            optionClones.add(optionClone);
        }

        clone.setItemOptions(optionClones);
        return clone;
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
            Item item = createItemOptionsBase(itemId, ConstItem.SERVER, 1);
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
        Item item = createItemOptionsBase(itemId, ConstItem.SERVER, 1);
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

    public static boolean isItemAutoPractice(int itemId) {
        return itemId == 521 || itemId == 1523 || itemId == 1524;
    }

}
