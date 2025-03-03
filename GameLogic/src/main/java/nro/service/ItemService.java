package nro.service;

import lombok.Getter;
import nro.model.item.Flag;
import nro.model.item.ItemMap;
import nro.model.player.Player;
import nro.server.LogServer;
import nro.server.manager.ItemManager;
import nro.server.network.Message;

import java.io.DataOutputStream;
import java.util.List;

@SuppressWarnings("ALL")
public class ItemService {

    @Getter
    private static final ItemService instance = new ItemService();

    public void sendShowListFlagBag(Player player) {
        try (Message message = new Message(-103);
             DataOutputStream writer = message.writer()) {

            List<Flag> flags = ItemManager.getInstance().getFlags();
            writer.writeByte(0);
            writer.writeByte(flags.size());

            for (Flag flag : flags) {
                var item = flag.itemFlagBag();
                writer.writeShort(item.getTemplate().id());
                writer.writeByte(item.getItemOptions().size());

                for (var option : item.getItemOptions()) {
                    writer.writeShort(option.getId());
                    writer.writeInt(option.getParam());
                }
            }

            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("sendShowListFlagBag: " + ex.getMessage(), ex);
        }
    }

    public void sendChangeFlag(Player player, int index) {
        try (Message message = new Message(-103);
             DataOutputStream writer = message.writer()) {
            writer.writeByte(1);
            writer.writeInt(player.getId());
            writer.writeByte(index);
            player.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("sendChangeFlag: " + ex.getMessage(), ex);
        }
    }

    public void sendImageFlag(Player player, int index, int icon) {
        try (Message message = new Message(-103);
             DataOutputStream writer = message.writer()) {
            writer.writeByte(2);
            writer.writeByte(index);
            writer.writeShort(icon);
            player.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("sendChangeFlag: " + ex.getMessage(), ex);
        }
    }

    public void sendDropItemMap(Player player, ItemMap itemMap) {
        try (Message message = new Message(68)) {
            DataOutputStream writer = message.writer();
            writer.writeShort(itemMap.getItemMapID());
            writer.writeShort(itemMap.getItem().getTemplate().id());
            writer.writeShort(itemMap.getX());
            writer.writeShort(itemMap.getY());
            writer.writeInt(player.getId());
            if (player.getId() != -2) {
                writer.writeShort(itemMap.getRange());
            }
            player.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("sendDropItemMap: " + ex.getMessage(), ex);
        }
    }

    public void sendRemoveItemMap(ItemMap itemMap) {
        try (Message message = new Message(-21)) {
            message.writer().writeShort(itemMap.getItemMapID());
            itemMap.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("sendRemoveItemMap: " + ex.getMessage(), ex);
        }
    }

}
