package nro.server.service.model.entity.player;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstError;
import nro.consts.ConstItem;
import nro.consts.ConstOption;
import nro.consts.ConstUseItem;
import nro.server.service.core.map.AreaService;
import nro.server.service.model.item.Item;
import nro.server.service.model.template.item.ItemOption;
import nro.server.system.LogServer;
import nro.server.service.core.player.InventoryService;
import nro.server.service.core.item.ItemService;
import nro.server.service.core.system.ServerService;
import nro.server.service.core.item.ItemFactory;
import nro.server.service.core.player.PlayerService;

import java.util.ArrayList;
import java.util.List;

import static nro.server.service.model.entity.npc.handler.ConMeo.*;

@Getter
@Setter
@SuppressWarnings("ALL")
public class PlayerInventory {

    private final Player player;
    private List<Item> itemsBody;
    private List<Item> itemsBag;
    private List<Item> itemsBox;

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
        try {
            if (this.isBagFull()) {
                ServerService.getInstance().sendChatGlobal(this.player.getSession(), null, "Hành trang đã đầy.", false);
                return false;
            }
            this.addItem(this.getItemsBag(), item);
            InventoryService.getInstance().sendItemToBags(player, 0);
        } catch (Exception e) {
            LogServer.LogException("Error addItemBag: " + e.getMessage(), e);
            return false;
        }
        return true;
    }

    public void addItemBody(Item item) {
        try {
            this.addItem(this.getItemsBody(), item);
            InventoryService.getInstance().sendItemToBodys(player);
        } catch (Exception e) {
            LogServer.LogException("Error addItemBody: " + e.getMessage(), e);
        }
    }

    public boolean addItemBox(Item item) {
        try {
            if (this.isBoxFull()) {
                ServerService.dialogMessage(player.getSession(), "Rương đồ đã đầy.");
                return false;
            }
            addItem(this.getItemsBox(), item);
            InventoryService.getInstance().sendItemsBox(player, 0);
        } catch (Exception e) {
            LogServer.LogException("Error addItemBox: " + e.getMessage(), e);
            return false;
        }
        return true;
    }

    private boolean addItem(List<Item> items, Item itemNew) {
        try {
            // check item add co dat chuan dieu kien chua
            if (itemNew == null || itemNew.getTemplate() == null) {
                LogServer.LogException("addItem: Item không hợp lệ. " + ConstError.ERROR_INVALID_ITEM);
                return false;
            }

            // neu item add co options thi add options mac dinh
            this.addOptionsDefault(itemNew);

            switch (itemNew.getTemplate().type()) {
                case ConstItem.TYPE_GOLD -> player.getPlayerCurrencies().addGold(itemNew.getQuantity());
                case ConstItem.TYPE_GEM -> player.getPlayerCurrencies().addGem(itemNew.getQuantity());
                case ConstItem.TYPE_RUBY -> player.getPlayerCurrencies().addRuby(itemNew.getQuantity());
                default -> {
                    if (itemNew.getTemplate().maxQuantity() > 1) {
                        for (Item itemInventory : items) {
                            if (itemInventory == null || itemInventory.getTemplate() == null) continue;

                            if (itemInventory.getTemplate().id() != itemNew.getTemplate().id() || !isSameOptions(itemInventory.getItemOptions(), itemNew.getItemOptions())) {
                                continue;
                            }
                            // kiểm tra số lượng item có vượt quá giới hạn không
                            int maxQuantity = itemNew.getTemplate().maxQuantity();

                            // số lượng còn lại trong hành trang
                            int spaceLeft = maxQuantity - itemInventory.getQuantity();

                            if (spaceLeft > 0) { // chỉ cộng dồn khi còn chỗ trống
                                // nếu số lượng item mới nhỏ hơn hoặc bằng số lượng còn trống
                                if (itemNew.getQuantity() <= spaceLeft) {
                                    // cộng dồn số lượng item mới vào item cũ
                                    itemInventory.addQuantity(itemNew.getQuantity());
                                    // xóa item mới
                                    this.disposeItem(itemNew);
                                    return true;
                                } else {
                                    // cộng dồn số lượng item mới vào item cũ
                                    itemInventory.addQuantity(spaceLeft);
                                    itemNew.subQuantity(spaceLeft);
                                }
                            }
                        }
                    }

                    // nếu item còn lại sau khi cộng dồn vẫn còn
                    while (itemNew.getQuantity() > 0) {
                        // tìm vị trí item null trong hành trang
                        short index = this.findIndexItemNullInventory(items);
                        if (index == -1) {
                            return false;
                        }
                        Item newItemStack = ItemFactory.getInstance().clone(itemNew);// tạo item mới
                        // số lượng item cần thêm vào hành trang
                        int addAmount = Math.min(itemNew.getQuantity(), itemNew.getTemplate().maxQuantity());
                        newItemStack.setQuantity(addAmount);
                        itemNew.subQuantity(addAmount);
                        items.set(index, newItemStack);
                    }
                    this.disposeItem(itemNew);
                }
            }
            return true;
        } catch (Exception e) {
            LogServer.LogException("Error addItem: " + e.getMessage(), e);
            return false;
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

    public void removeAllItemInventory(int type) {
        List<List<Item>> inventories = switch (type) {
            case TYPE_BOX -> List.of(this.itemsBox);
            case TYPE_BAG -> List.of(this.itemsBag);
            case TYPE_BODY -> List.of(this.itemsBody);
            case TYPE_ALL -> List.of(this.itemsBox, this.itemsBag, this.itemsBody);
            default -> List.of();
        };

        inventories.forEach(inventory ->
                inventory.replaceAll(item -> ItemFactory.getInstance().createItemNull())
        );

        this.sendInfoAfterEquipItem();
    }

    private void _______________THROW_ITEM______________() {
        // Xử lý người chơi vứt bỏ item
    }

    public void throwItem(byte where, byte index) {
        switch (where) {
            case ConstUseItem.THROW_ITEM_BODY -> {
                List<Item> itemBodys = this.getItemsBody();
                if (index < 0 || index >= itemBodys.size()) {
                    ServerService.dialogMessage(player.getSession(), "Đã xảy ra lỗi " + index);
                    return;
                }
                Item item = itemBodys.get(index);
                if (item == null) {
                    ServerService.dialogMessage(player.getSession(), "Đã xảy ra lỗi " + index);
                    return;
                }
                this.removeItemBody(index);
                InventoryService.getInstance().sendItemToBodys(player);
            }
            case ConstUseItem.THROW_ITEM_BAG -> {
                List<Item> itemsBag = this.getItemsBag();
                if (index < 0 || index >= itemsBag.size()) {
                    ServerService.dialogMessage(player.getSession(), "Đã xảy ra lỗi " + index);
                    return;
                }
                Item item = itemsBag.get(index);
                if (item == null) {
                    ServerService.dialogMessage(player.getSession(), "Đã xảy ra lỗi " + index);
                    return;
                }
                this.removeItemBag(index);
                InventoryService.getInstance().sendItemToBags(player, 0);
            }
            default ->
                    LogServer.LogWarning("Chưa xử lý xong where: " + where + " index: " + index + " player: " + player.getName());
        }
    }

    private void _______________SUB_QUANTITY_ITEM______________() {
        // Xử lý trừ số lượng item
    }

    public void subQuantityItemAllInventory(Item item, int quantity) {
        this.subQuantityItemsBag(item, quantity);
        this.subQuantityItemsBody(item, quantity);
        this.subQuantityItemsBox(item, quantity);
    }

    public void subQuantityItemsBag(Item item, int quantity) {
        this.subQuantityItem(this.getItemsBag(), item, quantity);
        this.player.getFashion().updateFashion();
        InventoryService.getInstance().sendItemToBags(player, 0);
    }

    public void subQuantityItemsBody(Item item, int quantity) {
        this.subQuantityItem(this.getItemsBody(), item, quantity);
        this.player.getFashion().updateFashion();
        InventoryService.getInstance().sendItemToBodys(player);
    }

    public void subQuantityItemsBox(Item item, int quantity) {
        this.subQuantityItem(this.getItemsBox(), item, quantity);
        this.player.getFashion().updateFashion();
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
            if (this.player.getPlayerTask().getTaskMain().getId() == 0 && itemBox.getTemplate().id() == 12) {
                player.getPlayerTask().checkDoneTask(0, 3);
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
            if (!this.addItemBox(itemBag)) return;
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
            if (!this.addItemBox(itemBody)) return;
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
            Item itemBody = this.putItemBodyForIndex(itemBag);
            this.itemsBag.set(index, itemBody);
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
        ServerService.getInstance().sendChatGlobal(this.player.getSession(), null, "Hành trang đã đầy.", false);
        return item;
    }

    private Item putItemBodyForIndex(Item item) {
        Item itemBody = ItemFactory.getInstance().createItemNull();
        try {
            int index;
            if (item != null && item.getTemplate() != null) {
                switch (item.getTemplate().type()) {
                    case ConstItem.TYPE_AO, ConstItem.TYPE_QUAN, ConstItem.TYPE_GANG, ConstItem.TYPE_GIAY,
                         ConstItem.TYPE_RADA_OR_NHAN, ConstItem.TYPE_CAI_TRANG_OR_AVATAR ->
                            index = item.getTemplate().type();
                    case ConstItem.TYPE_GIAP_LUYEN_TAP -> index = 6;
                    case ConstItem.TYPE_SACH_TUYET_KY -> index = 7;
                    case ConstItem.TYPE_FLAG_BAG -> index = 8;
                    case ConstItem.TYPE_MOUNT, ConstItem.TYPE_MOUNT_VIP -> index = 9;
                    default -> {
                        ServerService.getInstance().sendChatGlobal(this.player.getSession(), null, "Trang bị không phù hợp.", false);
                        return null;
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
        this.player.getPoints().calculateStats();
        this.player.getFashion().updateFashion();
        InventoryService inventoryService = InventoryService.getInstance();
        AreaService areaService = AreaService.getInstance();
        PlayerService playerService = PlayerService.getInstance();
        inventoryService.sendItemToBags(this.player, 0);
        inventoryService.sendItemToBodys(this.player);
        playerService.sendPointForMe(this.player);
        ItemService.getInstance().sendFlagBag(this.player);
        areaService.sendLoadPlayerInArea(player);// -30 ~ 7
        playerService.sendPlayerBody(this.player);
        areaService.sendSpeedPlayerInArea(player);//-30 ~ 8
    }

    public Item getItemTrade(int index, int quantity) {
        ServerService service = ServerService.getInstance();
        if (index < 0 || index >= this.itemsBag.size()) {
            return null;
        }

        Item item = this.itemsBag.get(index);
        if (item == null || item.getTemplate() == null) {
            return null;
        }

        if (item.getQuantity() < quantity && quantity > 99) {
            return null;
        }

        if (!item.getTemplate().isTrade()) {
            service.sendChatGlobal(this.player.getSession(), null, String.format("Không thể giao dịch %s", item.getTemplate().name()), false);
            return null;
        }

        boolean hasOption30 = this.doesItemHaveOptionId(item, ConstOption.KHONG_GIAO_DICH);
        if (hasOption30) {
            service.sendChatGlobal(this.player.getSession(), null, String.format("Không thể giao dịch %s", item.getTemplate().name()), false);
            return null;
        }

        return item;
    }

    private void _______________FIND_____________() {
    }

    public Item findItemInBag(int templateId) {
        return this.itemsBag.stream().filter(item -> item.getTemplate() != null && item.getTemplate().id() == templateId).findFirst().orElse(null);
    }

    public Item findItemInBody(int templateId) {
        return this.itemsBody.stream().filter(item -> item.getTemplate() != null && item.getTemplate().id() == templateId).findFirst().orElse(null);
    }

    public Item findItemInBox(int templateId) {
        return this.itemsBox.stream().filter(item -> item.getTemplate() != null && item.getTemplate().id() == templateId).findFirst().orElse(null);
    }

    public short findIndexItemNullInventory(List<Item> items) {
        for (short i = 0; i < items.size(); i++) {
            if (items.get(i).getTemplate() == null) {
                return i;
            }
        }
        return -1;
    }

    public boolean doesItemHaveOptionId(Item item, int optionId) {
        return item.getItemOptions().stream().anyMatch(itemOption -> itemOption.getId() == optionId);
    }

    private void _______________CHECK_______________() {
    }

    public boolean isBodyFull() {
        var constBody = this.itemsBody.stream().filter(item -> item.getTemplate() != null).count();
        return constBody >= this.itemsBody.size();
    }

    public boolean isBagFull() {
        long constBag = (int) this.itemsBag.stream().filter(item -> item.getTemplate() != null).count();
        return constBag >= this.itemsBag.size();
    }

    public boolean isBoxFull() {
        long constBox = (int) this.itemsBox.stream().filter(item -> item.getTemplate() != null).count();
        return constBox >= this.itemsBox.size();
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

    private boolean isSameOptions(List<ItemOption> options1, List<ItemOption> options2) {
        if (options1.size() != options2.size()) {
            return false;
        }

        for (ItemOption opt1 : options1) {
            boolean found = false;
            for (ItemOption opt2 : options2) {
                if (opt1.getId() == opt2.getId() && opt1.getParam() == opt2.getParam()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    private void _______________DISPOSE______________() {
    }

    public void disposeItem(Item item) {
        // nro.utils.Util.getMethodCaller();
        if (item != null) {
            item.dispose();
            item = null;
        }
    }

    public void dispose() {
        this.itemsBody.clear();
        this.itemsBag.clear();
        this.itemsBox.clear();
        this.itemsBag = null;
        this.itemsBody = null;
        this.itemsBox = null;
    }
}
