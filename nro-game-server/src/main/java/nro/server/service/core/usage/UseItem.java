package nro.server.service.core.usage;

import nro.consts.ConstItem;
import nro.consts.ConstUseItem;
import nro.server.service.core.item.ItemFactory;
import nro.server.service.core.system.ServerService;
import nro.server.service.model.item.Item;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.entity.player.PlayerInventory;
import nro.server.system.LogServer;
import nro.server.service.core.npc.NpcService;

import java.util.List;

public class UseItem {

    private static final class SingletonHolder {
        private static final UseItem instance = new UseItem();
    }

    public static UseItem getInstance() {
        return UseItem.SingletonHolder.instance;
    }

    public void useItem(Player player, byte type, byte where, byte index, short template) {
        try {
            switch (type) {
                case ConstUseItem.USE_ITEM, ConstUseItem.ACCEPT_USE_ITEM -> this.useItem(player, type, index, template);
                case ConstUseItem.CONFIRM_THROW_ITEM -> this.confirmThrowItem(player, type, where, index);
                case ConstUseItem.ACCEPT_THROW_ITEM -> player.getPlayerInventory().throwItem(where, index);
                default -> LogServer.LogWarning("useItem status: " + type + " player: " + player.getName());
            }
        } catch (Exception ex) {
            LogServer.LogException("useItem: " + ex.getMessage(), ex);
        }
    }

    public void getItem(Player player, int type, int index) {
        try {
            switch (type) {
                case ConstUseItem.MOVE_FROM_BOX_TO_BAG -> player.getPlayerInventory().moveFromBoxToBag(index);
                case ConstUseItem.MOVE_FROM_BAG_TO_BOX -> player.getPlayerInventory().moveFromBagToBox(index);
                case ConstUseItem.MOVE_FROM_BODY_TO_BOX -> player.getPlayerInventory().moveFromBodyToBox(index);
                case ConstUseItem.EQUIP_ITEM_FROM_BAG -> player.getPlayerInventory().equipItemFromBag(index);
                case ConstUseItem.UNEQUIP_ITEM_TO_BAG -> player.getPlayerInventory().unequipItemToBag(index);
                default -> LogServer.LogWarning("Get Item: Chua hoan thien status: " + type);
            }
        } catch (Exception ex) {
            LogServer.LogException("getItem: " + ex.getMessage(), ex);
        }
    }

    public void confirmThrowItem(Player player, byte type, byte where, byte index) {
        try {
            List<Item> items = switch (where) {
                case ConstUseItem.THROW_ITEM_BODY -> player.getPlayerInventory().getItemsBody();
                case ConstUseItem.THROW_ITEM_BAG -> player.getPlayerInventory().getItemsBag();
                default -> null;
            };

            if (items == null) {
                LogServer.LogWarning("confirmThrowItem status: " + type + " player: " + player.getName());
                return;
            }

            if (index < 0 || index >= items.size()) {
                ServerService.dialogMessage(player.getSession(), "Đã xảy ra lỗi");
                return;
            }

            Item item = items.get(index);
            if (item == null || item.getTemplate() == null) {
                ServerService.dialogMessage(player.getSession(), "Không có vật phẩm này!");
                return;
            }

            String info = String.format("Bạn có chắc muốn hủy bỏ (mất luôn)\n%dx %s", item.getQuantity(), item.getTemplate().name());
            UseItemService.getInstance().eventUseItem(player, type, where, index, info);

        } catch (Exception ex) {
            LogServer.LogException("confirmThrowItem: " + ex.getMessage(), ex);
        }
    }

    public void useItem(Player player, int type, int index, int template) {
        try {
            if (index == -1 && ItemFactory.isItemPea(template)) {
                ItemHandlerRegistry.getHandler(ConstItem.TYPE_PEA).use(player, type, index, null, template);
                return;
            }

            PlayerInventory inventory = player.getPlayerInventory();
            Item item = inventory.getItemsBag().get(index);

            if (item == null || item.getTemplate() == null) {
                NpcService.getInstance().sendNpcTalkUI(player, 5, "Có lỗi xảy ra, vui lòng thử lại!", -1);
                return;
            }

            IUseItemHandler handler = ItemHandlerRegistry.getHandler(item.getTemplate().type());

            if (handler == null) {
                LogServer.LogWarning("Không có handler cho item type: " + item.getTemplate().type());
                return;
            }

            handler.use(player, type, index, item, template);
        } catch (Exception ex) {
            LogServer.LogException("Error useItem: " + ex.getMessage(), ex);
        }
    }
}

