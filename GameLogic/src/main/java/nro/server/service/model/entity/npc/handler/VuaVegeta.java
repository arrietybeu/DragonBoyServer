package nro.server.service.model.entity.npc.handler;

import nro.consts.ConstNpc;
import nro.server.service.model.entity.npc.ANpcHandler;
import nro.server.service.model.entity.npc.Npc;
import nro.server.service.model.entity.player.Player;

@ANpcHandler({ConstNpc.VUA_VEGETA})
public class VuaVegeta extends Npc {

    public VuaVegeta(int tempId, int status, int mapId, int cx, int cy, int avatar) {
        super(tempId, status, mapId, cx, cy, avatar);
    }

    @Override
    public void openMenu(Player player) {

    }

    @Override
    public void openUIConfirm(Player player, int select) {

    }
}
