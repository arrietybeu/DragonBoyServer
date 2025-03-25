package nro.service.model.entity;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstItem;
import nro.service.model.entity.player.Player;
import nro.service.model.entity.player.PlayerInventory;
import nro.service.model.item.Item;
import nro.server.system.LogServer;

import java.util.List;

@Setter
@Getter
public class Fashion {

    private final BaseModel entity;

    private Player player;

    private byte flagPk = 0;
    private short head = -1;

    public Fashion(BaseModel entity) {
        this.entity = entity;
        this.setEntity(this.entity);
    }

    private void setEntity(BaseModel entity) {
        switch (entity) {
            case Player player -> this.player = player;
            default -> {
            }
        }
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
            LogServer.LogException("Fashion.getHead: " + exception.getMessage());
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
            LogServer.LogException("Fashion.getBody: " + exception.getMessage(), exception);
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
            LogServer.LogException("Fashion.getLeg: " + exception.getMessage(), exception);

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
            LogServer.LogException("Fashion.getMount: " + exception.getMessage(), exception);
            return -1;
        }
    }

    public short getFlagBag() {
        try {
            PlayerInventory playerInventory = this.player.getPlayerInventory();
            if (playerInventory != null) {
                List<Item> itemsBody = playerInventory.getItemsBody();
                if (itemsBody.get(8) != null && itemsBody.get(8).getTemplate() != null) {
                    return itemsBody.get(8).getTemplate().part();
                }
            }
            var taskMain = this.player.getPlayerTask().getTaskMain();
            if (taskMain.getId() == 3 && taskMain.getIndex() == 2) return 28;

        } catch (Exception exception) {
            LogServer.LogException("Fashion.getFlagBag: " + exception.getMessage(), exception);
            return -1;
        }
        return -1;
    }

}
