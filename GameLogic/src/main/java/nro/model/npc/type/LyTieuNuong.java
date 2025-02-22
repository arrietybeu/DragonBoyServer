package nro.model.npc.type;

import nro.consts.ConstMenu;
import nro.model.npc.ANpcHandler;
import nro.model.npc.Npc;
import nro.model.player.Player;
import nro.service.NpcService;

@ANpcHandler({54})
public class LyTieuNuong extends Npc {

    public LyTieuNuong(int tempId, int status, int mapId, int cx, int cy, int avatar) {
        super(tempId, status, mapId, cx, cy, avatar);
    }

    @Override
    public void openMenu(Player player) {
        String npcSay = "Mini game.";
        NpcService.getInstance().createMenu(player, this.getTempId(), ConstMenu.BASE_MENU, npcSay, " Mau lul");
    }

    @Override
    public void openUIConFirm(Player player, int select) {
    }
}
