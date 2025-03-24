package nro.service.model.npc.handler;

import nro.consts.ConstNpc;
import nro.service.model.npc.ANpcHandler;
import nro.service.model.npc.Npc;
import nro.service.model.entity.player.Player;
import nro.service.core.player.InventoryService;

@ANpcHandler({ConstNpc.RUONG_DO})
public class RuongDo extends Npc {

    public RuongDo(int tempId, int status, int mapId, int cx, int cy, int avatar) {
        super(tempId, status, mapId, cx, cy, avatar);
    }

    @Override
    public void openMenu(Player player) {
        InventoryService.getInstance().sendItemsBox(player, 0);
        InventoryService.getInstance().sendItemsBox(player, 1);
    }
}
