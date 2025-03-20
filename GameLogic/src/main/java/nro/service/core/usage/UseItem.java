package nro.service.core.usage;

import lombok.Getter;
import nro.consts.ConstItem;
import nro.service.core.item.ItemFactory;
import nro.service.model.item.Item;
import nro.service.model.player.Player;
import nro.service.model.player.PlayerInventory;
import nro.server.LogServer;
import nro.service.core.npc.NpcService;

public class UseItem {

    @Getter
    private static final UseItem instance = new UseItem();

    public void useItem(Player player, int index, int template) {
        try {
            if (index == -1 && ItemFactory.isItemPea(template)) {
                ItemHandlerRegistry.getHandler(ConstItem.TYPE_PEA).use(player, null, template);
                return;
            }
            PlayerInventory inventory = player.getPlayerInventory();
            Item item = inventory.getItemsBag().get(index);
            if (item == null || item.getTemplate() == null) {
                NpcService.getInstance().sendNpcTalkUI(player, 5, "Có lỗi xảy ra, vui lòng thử lại!", -1);
                return;
            }

            IItemHandler handler = ItemHandlerRegistry.getHandler(item.getTemplate().type());
            if (handler != null) {
                handler.use(player, item, template);
            } else {
                LogServer.LogWarning("Không có handler cho item type: " + item.getTemplate().type());
            }
        } catch (Exception ex) {
            LogServer.LogException("Error useItem: " + ex.getMessage(), ex);
        }
    }
}

