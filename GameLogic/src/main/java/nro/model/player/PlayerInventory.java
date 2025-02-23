package nro.model.player;

import lombok.Getter;
import lombok.Setter;
import nro.model.item.Item;
import nro.server.LogServer;
import nro.service.InventoryService;
import nro.service.ItemService;
import nro.service.Service;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlayerInventory {

    private final Player player;
    private final List<Item> itemsBody;
    private final List<Item> itemsBag;
    private final List<Item> itemsBox;

    public PlayerInventory(Player player) {
        this.player = player;
        this.itemsBody = new ArrayList<>();
        this.itemsBag = new ArrayList<>();
        this.itemsBox = new ArrayList<>();
    }

    private void ______________ADD_ITEM______________() {
    }

    public void addItemBag(Item item) {
        addItem(this.getItemsBag(), item);
        InventoryService.getInstance().sendItemToBags(player, 0);
    }

    public void addItemBody(Item item) {
        addItem(this.getItemsBody(), item);
        InventoryService.getInstance().sendItemToBodys(player);
    }

    public void addItemBox(Item item) {
        addItem(this.getItemsBox(), item);
        InventoryService.getInstance().sendItemsBox(player, 0);
    }

    private void addItem(List<Item> items, Item item) {
        try {
            this.addOptionsDefault(item);

            if (item.getTemplate().isUpToUp()) {
                for (Item it : items) {
                    if (it.getTemplate() == null) {
                        continue;
                    }
                    if (it.getTemplate().id() == item.getTemplate().id()) {
                        System.out.println("cong iem vo o cu");
                        int combinedQuantity = it.getQuantity() + item.getQuantity();
                        it.addQuantity(item.getQuantity());
                        return;
                    }
                }
            }

            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getTemplate() == null) {
                    items.set(i, ItemService.getInstance().clone(item));
                    item.setQuantity(0);
                    return;
                }
            }

            item.dispose();
        } catch (Exception e) {
            LogServer.LogException("Error addItem: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // neu item khong co options thi add options mac dinh
    private void addOptionsDefault(Item item) {
        if (item.getItemOptions().isEmpty()) {
            item.addOption(73, 0);
        }
    }

    private void ______________REMOVE_ITEM______________() {
    }

    private void removeItemBag(int index) {
        removeItem(this.getItemsBag(), index);
    }

    private void removeItemBody(int index) {
        removeItem(this.getItemsBody(), index);
    }

    private void removeItem(List<Item> items, int index) {
        if (index < 0 || index >= items.size()) {
            LogServer.LogWarning("removeItem: Index " + index + " không hợp lệ.");
            return;
        }
        items.set(index, ItemService.getInstance().createItemNull());
    }

    private void removeItem(List<Item> items, Item item) {
        Item itemNull = ItemService.getInstance().createItemNull();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == item) {
                items.set(i, itemNull);
                item.dispose();
                return;
            }
        }
        LogServer.LogWarning("removeItem: Không tìm thấy item để xóa.");
    }

    private void _______________THROW_ITEM______________() {
    }

    public void throwItem(byte where, byte index) {
        switch (where) {
            case 0: {
                List<Item> itemBodys = this.getItemsBody();
                if (index < 0 || index >= itemBodys.size()) {
                    Service.dialogMessage(player.getSession(), "Đã xảy ra lỗi " + index);
                    return;
                }
                Item item = itemBodys.get(index);
                if (item == null) {
                    Service.dialogMessage(player.getSession(), "Đã xảy ra lỗi " + index);
                    return;
                }
                this.removeItemBody(index);
                InventoryService.getInstance().sendItemToBodys(player);
                break;
            }
            default: {
                LogServer.LogWarning("Chưa xử lý xong where: " + where + " index: " + index + " player: " + player.getName());
                break;
            }
        }
    }

    private void _______________SUB_QUANTITY_ITEM______________() {
    }

    public void subQuantityItemsBag(Item item, int quantity) {
        this.subQuantityItem(this.getItemsBag(), item, quantity);
        InventoryService.getInstance().sendItemToBags(player, 0);
    }

    public void subQuantityItemsBody(Item item, int quantity) {
        this.subQuantityItem(this.getItemsBody(), item, quantity);
        InventoryService.getInstance().sendItemToBodys(player);
    }

    public void subQuantityItemsBox(Item item, int quantity) {
        this.subQuantityItem(this.getItemsBox(), item, quantity);
        InventoryService.getInstance().sendItemsBox(player, 0);
    }

    private void subQuantityItem(List<Item> items, Item item, int quantity) {
        for (Item it : items) {
            if (it == item) {
                it.subQuantity(quantity);
                if (it.getQuantity() <= 0) {
                    this.removeItem(items, item);
                }
            }
        }
    }

    private void _______________FIND_ITEM______________() {
    }


}
