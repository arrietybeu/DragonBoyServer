package nro.model.npc.type;

import nro.model.npc.Npc;
import nro.model.player.Player;
import nro.service.InventoryService;

public class RuongDo extends Npc {

    public RuongDo(int tempId, int status, int mapId, int cx, int cy, int avartar) {
        super(tempId, status, mapId, cx, cy, avartar);
    }


    @Override
    public void openMenu(Player player) {
        InventoryService.getInstance().sendItemToBoxs(player, 0);
        InventoryService.getInstance().sendItemToBoxs(player, 1);
    }

}
