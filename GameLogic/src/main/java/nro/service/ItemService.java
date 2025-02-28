package nro.service;

import lombok.Getter;
import nro.model.item.FlagBag;
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

            List<FlagBag> flagBags = ItemManager.getInstance().getFlagBags();
            writer.writeByte(0);
            writer.writeByte(flagBags.size());

            for (FlagBag flagBag : flagBags) {
                var item = flagBag.itemFlagBag();
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

}
