package nro.model.player;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstItem;
import nro.model.item.Item;
import nro.server.LogServer;
import nro.service.InventoryService;
import nro.service.core.ItemFactory;
import nro.service.PlayerService;
import nro.service.Service;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

@SuppressWarnings("unused")
public class PlayerInventory {

    private final Player player;
    private final List<Item> itemsBody;
    private final List<Item> itemsBag;
    private final List<Item> itemsBox;

    private int itemBodySize;
    private int itemBagSize;
    private int itemBoxSize;

    public PlayerInventory(Player player) {
        this.player = player;
        this.itemsBody = new ArrayList<>();
        this.itemsBag = new ArrayList<>();
        this.itemsBox = new ArrayList<>();
    }

    private void ______________ADD_ITEM______________() {
        // Xử lý người chơi nhận hoặc thêm item
    }

    public boolean addItemBag(Item item) {
        if (this.isBagFull()) {
            Service.dialogMessage(player.getSession(), "Hành trang đã đầy.");
            return false;
        }
        System.out.println("Add item bag: " + item.getTemplate().name());
        addItem(this.getItemsBag(), item);
        InventoryService.getInstance().sendItemToBags(player, 0);
        return true;
    }

    public void addItemBody(Item item) {
        addItem(this.getItemsBody(), item);
        InventoryService.getInstance().sendItemToBodys(player);
    }

    public void addItemBox(Item item) {
        if (this.isBoxFull()) {
            Service.dialogMessage(player.getSession(), "Rương đồ đã đầy.");
            return;
        }
        addItem(this.getItemsBox(), item);
        InventoryService.getInstance().sendItemsBox(player, 0);
    }

    private void addItem(List<Item> items, Item item) {
        try {
            this.addOptionsDefault(item);

            if (item.getTemplate() == null) {
                return;
            }
            if (item.getTemplate().isUpToUp()) {
                for (Item it : items) {
                    if (it.getTemplate() == null) {
                        continue;
                    }
                    if (it.getTemplate().id() == item.getTemplate().id()) {
                        // int combinedQuantity = it.getQuantity() + item.getQuantity();
                        it.addQuantity(item.getQuantity());
                        return;
                    }
                }
            }

            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getTemplate() == null) {
                    items.set(i, ItemFactory.getInstance().clone(item));
                    item.setQuantity(0);
                    return;
                }
            }

            item.dispose();
        } catch (Exception e) {
            LogServer.LogException("Error addItem: " + e.getMessage(), e);
        }
    }

    // neu item khong co options thi add options mac dinh
    private void addOptionsDefault(Item item) {
        if (item.getItemOptions().isEmpty()) {
            item.addOption(73, 0);
        }
    }

    private void ______________REMOVE_ITEM______________() {
        // Xử lý người chơi xóa item
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
        items.set(index, ItemFactory.getInstance().createItemNull());
    }

    private void removeItem(List<Item> items, Item item) {
        Item itemNull = ItemFactory.getInstance().createItemNull();
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
        // Xử lý người chơi vứt bỏ item
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
        // Xử lý trừ số lượng item
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
        for (int i = 0; i < items.size(); i++) {
            Item it = items.get(i);
            if (it.equals(item)) {
                it.subQuantity(quantity);
                if (it.getQuantity() <= 0) {
                    removeItem(items, item);
                }
                break;
            }
        }
    }

    private void _______________OPERATIONS_ITEM_______________() {
        // Xử lý player thao tác với item
    }

    public void moveFromBoxToBag(int index) {
        if (index < 0 || index >= this.itemsBox.size()) {
            return;
        }
        Item itemBox = this.itemsBox.get(index);
        if (itemBox != null && itemBox.getTemplate() != null) {
            if (itemBox.getTemplate().id() == 12) {
                player.getPlayerTask().checkDoneTaskGetItemBox();
            }
            this.addItemBag(itemBox);
            if (itemBox.getQuantity() == 0) {
                Item itemNull = ItemFactory.getInstance().createItemNull();
                this.itemsBox.set(index, itemNull);
            }
            InventoryService.getInstance().sendItemsBox(this.player, 0);
        }
    }

    public void moveFromBagToBox(int index) {
        if (index < 0 || index >= this.itemsBag.size()) {
            return;
        }
        Item itemBag = this.itemsBag.get(index);
        if (itemBag != null && itemBag.getTemplate() != null) {
            this.addItemBox(itemBag);
            if (itemBag.getQuantity() == 0) {
                Item itemNull = ItemFactory.getInstance().createItemNull();
                this.itemsBag.set(index, itemNull);
            }
            InventoryService.getInstance().sendItemToBags(this.player, 0);
        }
    }

    public void moveFromBodyToBox(int index) throws RuntimeException {
        if (index < 0 || index >= this.itemsBody.size()) {
            return;
        }
        Item itemBody = this.itemsBody.get(index);
        if (itemBody != null && itemBody.getTemplate() != null) {
            this.addItemBox(itemBody);
            if (itemBody.getQuantity() == 0) {
                Item itemNull = ItemFactory.getInstance().createItemNull();
                this.itemsBody.set(index, itemNull);
            }
            InventoryService.getInstance().sendItemToBodys(this.player);
        }
    }

    public void equipItemFromBag(int index) throws RuntimeException {
        if (index < 0 || index >= this.itemsBag.size()) {
            return;
        }
        Item itemBag = this.itemsBag.get(index);
        if (itemBag != null && itemBag.getTemplate() != null) {
            this.itemsBag.set(index, this.putItemBodyForIndex(itemBag));
            this.sendInfoAfterEquipItem();
        }
    }

    public void unequipItemToBag(int index) throws RuntimeException {
        if (index < 0 || index >= this.itemsBody.size()) {
            return;
        }
        Item itemBody = this.itemsBody.get(index);
        if (itemBody != null && itemBody.getTemplate() != null) {
            this.itemsBody.set(index, this.putItemBag(itemBody));
            this.sendInfoAfterEquipItem();
        }
    }

    private Item putItemBag(Item item) throws RuntimeException {
        for (int i = 0; i < itemsBag.size(); i++) {
            Item itemBag = itemsBag.get(i);
            if (itemBag == null || itemBag.getTemplate() == null) {
                itemsBag.set(i, item);
                return ItemFactory.getInstance().createItemNull();
            }
        }
        return item;
    }

    private Item putItemBodyForIndex(Item item) {
        Item itemBody = item;
        try {
            int index = -1;
            if (item != null && item.getTemplate() != null) {
                switch (item.getTemplate().type()) {
                    case ConstItem.AO:
                    case ConstItem.QUAN:
                    case ConstItem.GANG:
                    case ConstItem.GIAY:
                    case ConstItem.RADA_OR_NHAN:
                    case ConstItem.CAI_TRANG_OR_AVATAR:
                        index = item.getTemplate().type();
                        break;
                    case ConstItem.GIAP_LUYEN_TAP:
                        index = 6;
                        break;
                    case ConstItem.SACH_TUYET_KY:
                        index = 7;
                        break;
                    case ConstItem.FLAG_BAG:
                        index = 8;
                        break;
                    case ConstItem.MOUNT:
                    case ConstItem.MOUNT_VIP:
                        index = 9;
                        break;
                    // TODO mini pet index = 10
                    default: {
                        Service.getInstance().sendChatGlobal(this.player.getSession(), null, "Trang bị không phù hợp.", false);
                        return itemBody;
                    }
                }
                itemBody = this.itemsBody.get(index);// lay item o body item tai (khong co gi) set item itemBody = null

                this.itemsBody.set(index, item);
                return itemBody;
            }
        } catch (Exception ex) {
            LogServer.LogException("Error putItemBodyForIndex: " + ex.getMessage(), ex);
        }
        return itemBody;
    }

    private void sendInfoAfterEquipItem() {
        InventoryService inventoryService = InventoryService.getInstance();
        PlayerService playerService = PlayerService.getInstance();
        inventoryService.sendItemToBags(this.player, 0);
        inventoryService.sendItemToBodys(this.player);
        playerService.sendPlayerBody(this.player);
        playerService.sendPointForMe(this.player);
    }

    private void _______________FIND_ITEM______________() {
    }

    private void _______________CHECK_SIZE_INVENTORY______________() {
    }

    public boolean isBodyFull() {
        return this.itemsBody.stream().filter(item -> item.getTemplate() != null).count() >= itemBodySize;
    }

    public boolean isBagFull() {
        long constBag = (int) this.itemsBag.stream().filter(item -> item.getTemplate() != null).count();
        return constBag >= itemBagSize;
    }

    public boolean isBoxFull() {
        long constBox = (int) this.itemsBox.stream().filter(item -> item.getTemplate() != null).count();
        return constBox >= itemBoxSize;
    }

    public byte getCountEmptyBag() {
        return getCountEmptyListItem(this.itemsBag);
    }

    public byte getCountEmptyListItem(List<Item> list) {
        byte count = 0;
        for (Item item : list) {
            if (item.getTemplate() != null) {
                count++;
            }
        }
        return count;
    }
}
