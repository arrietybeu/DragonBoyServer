package nro.service;

import lombok.Getter;
import nro.consts.ConstUseItem;
import nro.model.item.Item;
import nro.model.player.Player;
import nro.server.LogServer;
import nro.server.network.Message;
import nro.service.core.UseItem;

import java.io.DataOutputStream;
import java.util.List;

public class UseItemService {

    @Getter
    private static final UseItemService instance = new UseItemService();

    public void useItem(Player player, byte type, byte where, byte index, short template) {
        try {
            switch (type) {
                case ConstUseItem.USE_ITEM: {
                    UseItem.getInstance().useItem(player, index, template);
                    break;
                }
                case ConstUseItem.CONFIRM_THROW_ITEM: {
                    this.confirmThrowItem(player, type, where, index);
                    break;
                }
                case ConstUseItem.ACCEPT_THROW_ITEM: {
                    player.getPlayerInventory().throwItem(where, index);
                    break;
                }
                default: {
                    LogServer.LogWarning("useItem type: " + type + " player: " + player.getName());
                    break;
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("useItem: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void getItem(Player player, int type, int index) {
        try {
            switch (type) {
                case ConstUseItem.MOVE_FROM_BOX_TO_BAG: {
                    player.getPlayerInventory().moveFromBoxToBag(index);
                    break;
                }
                case ConstUseItem.MOVE_FROM_BAG_TO_BOX: {
                    player.getPlayerInventory().moveFromBagToBox(index);
                    break;
                }
                case ConstUseItem.MOVE_FROM_BODY_TO_BOX: {
                    player.getPlayerInventory().moveFromBodyToBox(index);
                    break;
                }
                case ConstUseItem.EQUIP_ITEM_FROM_BAG: {
                    player.getPlayerInventory().equipItemFromBag(index);
                    break;
                }
                case ConstUseItem.UNEQUIP_ITEM_TO_BAG: {
                    player.getPlayerInventory().unequipItemToBag(index);
                    break;
                }
                default: {
                    LogServer.LogWarning("Get Item: Chua hoan thien type: " + type);
                    break;
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("getItem: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void confirmThrowItem(Player player, byte type, byte where, byte index) {
        try {
            switch (where) {
                case 0: {
                    // item body
                    List<Item> itemsBody = player.getPlayerInventory().getItemsBody();
                    if (index < 0 || index >= itemsBody.size()) {
                        Service.dialogMessage(player.getSession(), "Đã xảy ra lỗi");
                        return;
                    }
                    Item item = itemsBody.get(index);
                    if (item == null || item.getTemplate() == null) {
                        Service.dialogMessage(player.getSession(), "Không có vật phẩm này!");
                        return;
                    }
                    String info = String.format("Bạn có chắc muốn hủy bỏ (mất luôn)\n%d %s", item.getQuantity(), item.getTemplate().name());
                    this.eventUseItem(player, type, where, index, info);
                    break;

                }
                default: {
                    LogServer.LogWarning("confirmThrowItem type: " + type + " player: " + player.getName());
                    break;
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("confirmThrowItem: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void eventUseItem(Player player, int itemAction, int where, int index, String info) {
        try (Message message = new Message(-43)) {
            DataOutputStream data = message.writer();
            data.writeByte(itemAction);
            data.writeByte(where);
            data.writeByte(index);
            data.writeUTF(info);
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("eventUseItem: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
