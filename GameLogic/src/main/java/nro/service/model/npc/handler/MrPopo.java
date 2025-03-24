package nro.service.model.npc.handler;

import nro.consts.ConstNpc;
import nro.service.model.npc.ANpcHandler;
import nro.service.model.npc.Npc;
import nro.service.model.entity.player.Player;
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
