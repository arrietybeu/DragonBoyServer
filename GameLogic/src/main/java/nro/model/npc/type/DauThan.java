package nro.model.npc.type;

import nro.consts.ConstNpc;
import nro.model.npc.ANpcHandler;
import nro.model.npc.Npc;
import nro.model.player.Player;

@ANpcHandler({ConstNpc.DAU_THAN})
public class DauThan extends Npc {

    public DauThan(int tempId, int status, int mapId, int cx, int cy, int avatar) {
        super(tempId, status, mapId, cx, cy, avatar);
    }

    @Override
    public void openMenu(Player player) {
    }
}
