package nro.service;

import lombok.Getter;
import nro.model.item.Flag;
import nro.model.player.Player;
import nro.server.LogServer;
import nro.server.manager.ItemManager;
import nro.server.network.Message;

import java.io.DataOutputStream;
import java.util.List;

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
            LogServer.LogException("sendShowListFlagBag: " + ex.getMessage());
            ex.printStackTrace();
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
            LogServer.LogException("sendChangeFlag: " + ex.getMessage());
            ex.printStackTrace();
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
            LogServer.LogException("sendChangeFlag: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
