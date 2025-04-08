package nro.server.service.core.usage.handler;

import nro.consts.ConstItem;
import nro.server.service.core.usage.AUseItemHandler;
import nro.server.service.core.usage.IUseItemHandler;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.item.Item;
import nro.server.system.LogServer;

@AUseItemHandler({ConstItem.TYPE_MOUNT, ConstItem.TYPE_MOUNT_VIP})
public class UseItemMountHandler implements IUseItemHandler {

    @Override
    public void use(Player player, int type, int index, Item item, int... id) {
        try {
            player.getPlayerInventory().equipItemFromBag(index);
        } catch (Exception exception) {
            LogServer.LogException("UseItemMountHandler: " + exception.getMessage(), exception);
        }
    }
}
