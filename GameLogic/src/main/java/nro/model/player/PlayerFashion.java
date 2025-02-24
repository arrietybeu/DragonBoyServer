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
                    short head = itemsBody.get(5).getTemplate().head();
                    if (head != -1) {
                        return head;
                    }
                    short part = itemsBody.get(5).getTemplate().part();
                    if (part != -1) {
                        return part;
                    }
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
                    short body = itemsBody.get(5).getTemplate().body();
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
                    short leg = itemsBody.get(5).getTemplate().leg();
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

    public short getMount() {
        try {
            PlayerInventory playerInventory = this.player.getPlayerInventory();
            if (playerInventory != null) {
                List<Item> itemsBody = playerInventory.getItemsBody();
                if (itemsBody.get(9) != null && itemsBody.get(9).getTemplate() != null) {
                    return itemsBody.get(9).getTemplate().id();
                }
            }
            return -1;
        } catch (Exception exception) {
            LogServer.LogException("PlayerFashion.getMount: " + exception.getMessage());
            exception.printStackTrace();
            return -1;
        }

    }

    public short getFlagBag() {
        return -1;
    }

}
