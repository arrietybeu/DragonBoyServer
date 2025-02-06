package nro.service;

import lombok.Getter;
import nro.model.player.Player;
import nro.network.Message;
import nro.server.LogServer;

public class InventoryService {

    @Getter
    private static final InventoryService instance = new InventoryService();

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

}
