package nro.service.model.model.npc.type;

import nro.consts.ConstNpc;
import nro.service.model.model.npc.ANpcHandler;
import nro.service.model.model.npc.Npc;
import nro.service.model.model.player.Player;
import nro.service.core.npc.NpcService;
import nro.service.core.system.ServerService;

@ANpcHandler({ConstNpc.MR_POPO})
public class MrPopo extends Npc {

    public MrPopo(int tempId, int status, int mapId, int cx, int cy, int avatar) {
        super(tempId, status, mapId, cx, cy, avatar);
    }

    @Override
    public void openMenu(Player player) {
        ServerService.getInstance().sendHideWaitDialog(player);
        NpcService.getInstance().sendNpcChatAllPlayerInArea(player, this, "Xin chào");

    }

    @Override
    public void openUIConFirm(Player player, int select) {
    }
}
