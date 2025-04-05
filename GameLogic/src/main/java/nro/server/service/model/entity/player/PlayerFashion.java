package nro.server.service.model.entity.player;

import nro.consts.ConstItem;
import nro.server.system.LogServer;
import nro.server.service.model.entity.Fashion;
import nro.server.service.model.item.Item;

import java.util.List;

public class PlayerFashion extends Fashion {

    private final Player player;

    public PlayerFashion(Player player) {
        this.player = player;
    }

    @Override
    public byte getFlagPk() {
        return 0;
    }

    @Override
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
            return super.head;
        } catch (Exception exception) {
            LogServer.LogException("Fashion.getHead: " + exception.getMessage(), exception);
            return super.head;
        }
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public short getAura() {
        return -1;
    }

    @Override
    public byte getEffSetItem() {
        return -1;
    }

    @Override
    public short getIdHat() {
        return -1;
    }

}
