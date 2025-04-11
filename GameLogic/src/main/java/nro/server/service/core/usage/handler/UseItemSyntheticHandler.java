package nro.server.service.core.usage.handler;


import nro.consts.ConstItem;
import nro.consts.ConstPlayer;
import nro.server.service.core.map.MapService;
import nro.server.service.core.usage.AUseItemHandler;
import nro.server.service.core.usage.IUseItemHandler;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.item.Item;
import nro.server.system.LogServer;

@AUseItemHandler({ConstItem.TYPE_SYNTHESIS})
public class UseItemSyntheticHandler implements IUseItemHandler {

    @Override
    public void use(Player player, int type, int index, Item item, int... id) {
        try {
            var idItem = item.getTemplate().id();
            switch (idItem) {
                case ConstItem.GOI_10_VIEN_CAPSULE -> {
                    this.useCapsuleBayHandler(player);
                    player.getPlayerInventory().subQuantityItemsBag(item, 1);
                }
                case ConstItem.VIEN_CAPSULE_DAC_BIET -> useCapsuleBayHandler(player);
                default ->
                        LogServer.LogWarning("UseItemSyntheticHandler: [" + idItem + "] " + item.getTemplate().name() + " not found");
            }
        } catch (Exception e) {
            LogServer.LogException("UseItemSyntheticHandler: " + e.getMessage(), e);
        }
    }

    private void useCapsuleBayHandler(Player player) {
        player.getPlayerState().setTypeTransport(ConstPlayer.TYPE_TRANSPORT_CAPSULE);
        MapService.getInstance().sendMapTransport(player);
    }
}
