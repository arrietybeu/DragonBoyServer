package nro.model.npc.type;

import nro.consts.ConstNpc;
import nro.model.npc.ANpcHandler;
import nro.model.npc.Npc;
import nro.model.player.Player;
import nro.service.NpcService;
import nro.service.Service;

@ANpcHandler({ConstNpc.MR_POPO})
public class MrPopo extends Npc {

    public MrPopo(int tempId, int status, int mapId, int cx, int cy, int avatar) {
        super(tempId, status, mapId, cx, cy, avatar);
    }

    @Override
    public void openMenu(Player player) {
        Service.getInstance().sendHideWaitDialog(player);
        NpcService.getInstance().sendNpcChatAllPlayerInArea(player, this, "Xin chào");
    }

    @Override
    public void openUIConFirm(Player player, int select) {
    }
}
