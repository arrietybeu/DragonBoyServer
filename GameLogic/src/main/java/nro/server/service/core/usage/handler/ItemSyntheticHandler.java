package nro.server.service.core.usage.handler;


import nro.consts.ConstItem;
import nro.server.service.core.usage.AUseItemHandler;
import nro.server.service.core.usage.IUseItemHandler;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.item.Item;
import nro.server.system.LogServer;

@AUseItemHandler({ConstItem.TYPE_SYNTHESIS})
public class ItemSyntheticHandler implements IUseItemHandler {
    @Override
    public void use(Player player, int type, int index, Item item, int... id) {
        try {
            var idItem = item.getTemplate().id();
            switch (idItem) {
                case ConstItem.GOI_10_VIEN_CAPSULE -> {

                }
                default ->
                        LogServer.LogWarning("ItemSyntheticHandler: [" + idItem + "] " + item.getTemplate().name() + " not found");
            }
        } catch (Exception e) {
            LogServer.LogException("ItemSyntheticHandler: " + e.getMessage(), e);
        }
    }

    private void useCapsuleBayHandler(Player player, Item item) {

    }
}
