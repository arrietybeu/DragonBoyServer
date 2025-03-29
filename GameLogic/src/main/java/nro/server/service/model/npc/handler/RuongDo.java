package nro.server.service.model.npc.handler;

import nro.consts.ConstNpc;
import nro.server.service.model.npc.ANpcHandler;
import nro.server.service.model.npc.Npc;
import nro.server.service.model.entity.player.Player;
import nro.server.service.core.player.InventoryService;

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
