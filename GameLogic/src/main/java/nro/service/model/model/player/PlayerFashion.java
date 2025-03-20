package nro.service.model.model.player;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstItem;
import nro.service.model.model.item.Item;
import nro.server.LogServer;

import java.util.List;

@Setter
@Getter
public class PlayerFashion {

    private final Player player;

    private byte flagPk = 0;
    private short head = -1;

    public PlayerFashion(Player player) {
        this.player = player;
    }

    public short getHead() {
        try {
            PlayerInventory playerInventory = this.player.getPlayerInventory();
            var index = ConstItem.TYPE_CAI_TRANG_OR_AVATAR;
            if (playerInventory != null) {
                List<Item> itemsBody = playerInventory.getItemsBody();
                if (itemsBody.get(index) != null && itemsBody.get(index).getTemplate() != null) {
                    short head = itemsBody.get(index).getTemplate().head();
                    if (head != -1) {
                        return head;
                    }
                    short part = itemsBody.get(index).getTemplate().part();
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
            var index = ConstItem.TYPE_CAI_TRANG_OR_AVATAR;
            if (playerInventory != null) {
                List<Item> itemsBody = playerInventory.getItemsBody();
                if (itemsBody.get(index) != null && itemsBody.get(index).getTemplate() != null) {
                    short body = itemsBody.get(index).getTemplate().body();
                    if (body != -1) {
                        return body;
                    }
                }
                if (itemsBody.getFirst() != null && itemsBody.getFirst().getTemplate() != null) {
                    return itemsBody.getFirst().getTemplate().part();
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
            var index = ConstItem.TYPE_CAI_TRANG_OR_AVATAR;
            var indexQuan = ConstItem.TYPE_QUAN;
            if (playerInventory != null) {
                List<Item> itemsBody = playerInventory.getItemsBody();
                if (itemsBody.get(index) != null && itemsBody.get(index).getTemplate() != null) {
                    short leg = itemsBody.get(index).getTemplate().leg();
                    if (leg != -1) {
                        return leg;
                    }
                }
                if (itemsBody.get(indexQuan) != null && itemsBody.get(indexQuan).getTemplate() != null) {
                    return itemsBody.get(indexQuan).getTemplate().part();
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
            LogServer.LogException("PlayerFashion.getMount: " + exception.getMessage(), exception);
            return -1;
        }
    }

    public short getFlagBag() {
        PlayerInventory playerInventory = this.player.getPlayerInventory();
        if (playerInventory != null) {
            List<Item> itemsBody = playerInventory.getItemsBody();
            if (itemsBody.get(8) != null && itemsBody.get(8).getTemplate() != null) {
                return itemsBody.get(8).getTemplate().part();
            }
        }

        var taskMain = this.player.getPlayerTask().getTaskMain();
        if (taskMain.getId() == 3 && taskMain.getIndex() == 2) return 28;
        return -1;
    }

}
