package nro.server.service.model.entity.npc.handler;

import nro.consts.ConstNpc;
import nro.server.service.model.entity.npc.ANpcHandler;
import nro.server.service.model.entity.npc.Npc;
import nro.server.service.model.entity.player.Player;

@ANpcHandler({ConstNpc.BO_MONG})
public class BoMong extends Npc {

    public BoMong(int tempId, int status, int mapId, int cx, int cy, int avatar) {
        super(tempId, status, mapId, cx, cy, avatar);
    }

    @Override
    public void openMenu(Player player) {
        if (player.getPlayerTask().checkDoneTaskTalkNpc(this)) return;
    }

    @Override
    public void openUIConfirm(Player player, int select) {
    }

}
