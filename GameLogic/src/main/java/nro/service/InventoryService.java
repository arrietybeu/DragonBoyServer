package nro.service;

import lombok.Getter;
import nro.model.item.Item;
import nro.model.player.Player;
import nro.server.network.Message;
import nro.server.LogServer;

import java.io.DataOutputStream;
import java.util.List;

public class InventoryService {

    @Getter
    private static final InventoryService instance = new InventoryService();

    public void throwItem(Player player, byte where, byte index) {
        switch (where) {
            case 0: {
                List<Item> itemBodys = player.getPlayerInventory().getItemsBody();
                if (index < 0 || index >= itemBodys.size()) {
                    Service.dialogMessage(player.getSession(), "Đã xảy ra lỗi " + index);
                    return;
                }
                Item item = itemBodys.get(index);
                if (item == null) {
                    Service.dialogMessage(player.getSession(), "Đã xảy ra lỗi " + index);
                    return;
                }
                this.removeItemBody(player, index);
                this.sendItemToBodys(player);
                break;
            }
            default: {
                LogServer.LogWarning("Chưa xử lý xong where: " + where + " index: " + index + " player: " + player.getName());
                break;
            }
        }
    }

    public void removeItemBag(Player player, int index) {
        removeItem(player.getPlayerInventory().getItemsBag(), index);
    }

    public void removeItemBody(Player player, int index) {
        removeItem(player.getPlayerInventory().getItemsBody(), index);
    }

    public void removeItem(List<Item> items, int index) {
        Item item = ItemService.getInstance().createItemNull();
        items.set(index, item);
    }

    public void sendFlagBag(Player player) {
        try (Message msg = new Message(-64)) {
            msg.writer().writeInt(player.getId());
            msg.writer().writeShort(player.getPlayerFashion().getFlagBag());
            player.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
            LogServer.LogException("Error sendFlagBag: " + e.getMessage() + " player: " + player.getId());
        }
    }

    /**
     * public static final byte BODY = -37;
     * <p>
     * public static final byte BAG = -36;
     * <p>
     * public static final byte BOX = -35;
     */

    public void sendItemToBodys(Player player) {
        try (Message message = new Message(-37)) {
            DataOutputStream data = message.writer();
            List<Item> itemBodys = player.getPlayerInventory().getItemsBody();
            data.writeByte(0);
            data.writeShort(player.getPlayerFashion().getHead());
            PlayerService.getInstance().sendInventoryForPlayer(data, itemBodys);
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("sendItemToBodys: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void sendItemToBags(Player player, int type) {
        try (Message message = new Message(-36)) {
            DataOutputStream data = message.writer();
            List<Item> itemsBag = player.getPlayerInventory().getItemsBag();
            data.writeByte(type);
            switch (type) {
                case 0: {
                    PlayerService.getInstance().sendInventoryForPlayer(data, itemsBag);
                    break;
                }
            }
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("sendItemToBags: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void sendItemsBox(Player player, int type) {
        try (Message message = new Message(-35)) {
            DataOutputStream data = message.writer();
            List<Item> itemsBox = player.getPlayerInventory().getItemsBox();
            data.writeByte(type);
            switch (type) {
                case 0: {
                    PlayerService.getInstance().sendInventoryForPlayer(data, itemsBox);
                    break;
                }
                case 1: {
                    // open
                    break;
                }
            }
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("sendItemToBoxs: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
