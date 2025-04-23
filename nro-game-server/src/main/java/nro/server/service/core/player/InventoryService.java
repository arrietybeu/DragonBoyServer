package nro.server.service.core.player;

import nro.server.service.model.item.Item;
import nro.server.service.model.entity.player.Player;
import nro.server.network.Message;
import nro.server.system.LogServer;

import java.io.DataOutputStream;
import java.util.List;

public class InventoryService {

    private static final class SingletonHolder {
        private static final InventoryService instance = new InventoryService();
    }

    public static InventoryService getInstance() {
        return InventoryService.SingletonHolder.instance;
    }

    public void sendFlagBag(Player player) {
        try (Message msg = new Message(-64)) {
            msg.writer().writeInt(player.getId());
            msg.writer().writeShort(player.getFashion().getFlagBag());
            player.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
            LogServer.LogException("Error sendFlagBag: " + e.getMessage() + " player: " + player.getId());
        }
    }

    public void sendItemToBodys(Player player) {
        try (Message message = new Message(-37)) {
            DataOutputStream data = message.writer();
            List<Item> itemBodys = player.getPlayerInventory().getItemsBody();
            data.writeByte(0);
            data.writeShort(player.getFashion().getHead());
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
            LogServer.LogException("sendItemToBags: " + ex.getMessage(), ex);
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
            LogServer.LogException("sendItemToBoxs: " + ex.getMessage(), ex);
        }
    }

}
