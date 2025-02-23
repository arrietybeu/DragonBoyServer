package nro.model.player;

import lombok.Getter;
import lombok.Setter;
import nro.model.item.Item;
import nro.server.LogServer;

import java.util.List;

@Setter
@Getter
public class PlayerFashion {

    private final Player player;

    private byte flag = 0;

    private short head = -1;

    public PlayerFashion(Player player) {
        this.player = player;
    }

    public short getHead() {
        try {
            PlayerInventory playerInventory = this.player.getPlayerInventory();
            if (playerInventory != null) {
                List<Item> itemsBody = playerInventory.getItemsBody();
                if (itemsBody.get(5) != null && itemsBody.get(5).getTemplate() != null) {
                    return (short) itemsBody.get(5).getTemplate().head();
                }
            }
            return head;
        } catch (Exception exception) {
            LogServer.LogException("PlayerFashion.getHead: " + exception.getMessage());
            exception.printStackTrace();
            return head;
        }
    }

    public short getBody() {
        try {
            PlayerInventory playerInventory = this.player.getPlayerInventory();
            if (playerInventory != null) {
                List<Item> itemsBody = playerInventory.getItemsBody();
                if (itemsBody.get(5) != null && itemsBody.get(5).getTemplate() != null) {
                    short body = (short) itemsBody.get(5).getTemplate().body();
                    if (body != -1) {
                        return body;
                    }
                }
                if (itemsBody.get(0) != null && itemsBody.get(0).getTemplate() != null) {
                    return itemsBody.get(0).getTemplate().part();
                }
            }
            return -1;
        } catch (Exception exception) {
            LogServer.LogException("PlayerFashion.getBody: " + exception.getMessage());
            exception.printStackTrace();
            return -1;
        }
    }

    public short getLeg() {
        try {
            PlayerInventory playerInventory = this.player.getPlayerInventory();
            if (playerInventory != null) {
                List<Item> itemsBody = playerInventory.getItemsBody();
                if (itemsBody.get(5) != null && itemsBody.get(5).getTemplate() != null) {
                    short leg = (short) itemsBody.get(5).getTemplate().leg();
                    if (leg != -1) {
                        return leg;
                    }
                }
                if (itemsBody.get(1) != null && itemsBody.get(1).getTemplate() != null) {
                    return itemsBody.get(1).getTemplate().part();
                }
            }
            return -1;
        } catch (Exception exception) {
            LogServer.LogException("PlayerFashion.getLeg: " + exception.getMessage());
            exception.printStackTrace();
            return -1;
        }
    }

    public short getFlagBag() {
        return -1;
    }

}
