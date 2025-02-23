package nro.model.player;

import lombok.Getter;
import lombok.Setter;
import nro.server.LogServer;

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
                if (playerInventory.getItemsBody().get(5) != null && playerInventory.getItemsBody().get(5).getTemplate() != null) {
                    return (short) playerInventory.getItemsBody().get(5).getTemplate().head();
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
                if (playerInventory.getItemsBody().get(5) != null && playerInventory.getItemsBody().get(5).getTemplate() != null) {
                    short body = (short) playerInventory.getItemsBody().get(5).getTemplate().body();
                    if (body != -1) {
                        return body;
                    }
                }
                if (playerInventory.getItemsBody().get(0) != null && playerInventory.getItemsBody().get(0).getTemplate() != null) {
                    return playerInventory.getItemsBody().get(0).getTemplate().part();
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
                if (playerInventory.getItemsBody().get(5) != null && playerInventory.getItemsBody().get(5).getTemplate() != null) {
                    short leg = (short) playerInventory.getItemsBody().get(5).getTemplate().leg();
                    if (leg != -1) {
                        return leg;
                    }
                }
                if (playerInventory.getItemsBody().get(1) != null && playerInventory.getItemsBody().get(1).getTemplate() != null) {
                    return playerInventory.getItemsBody().get(1).getTemplate().part();
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
