package nro.model.npc.type;

import nro.consts.ConstNpc;
import nro.model.npc.ANpcHandler;
import nro.model.npc.Npc;
import nro.model.player.Player;

@ANpcHandler({ConstNpc.BUNMA})
public class Bunma extends Npc {

    public Bunma(int tempId, int status, int mapId, int cx, int cy, int avartar) {
        super(tempId, status, mapId, cx, cy, avartar);
    }

    @Override
    public void openUIConFirm(Player player, int select) {
    }

}
