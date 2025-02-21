package nro.model.npc.type;

import nro.consts.ConstNpc;
import nro.model.npc.ANpcHandler;
import nro.model.npc.Npc;
import nro.model.player.Player;
import nro.service.InventoryService;

@ANpcHandler({ConstNpc.RUONG_DO})
public class RuongDo extends Npc {

    public RuongDo(int tempId, int status, int mapId, int cx, int cy, int avatar) {
        super(tempId, status, mapId, cx, cy, avatar);
    }

    @Override
    public void openMenu(Player player) {
        InventoryService.getInstance().sendItemToBoxs(player, 0);
        InventoryService.getInstance().sendItemToBoxs(player, 1);
    }
}
