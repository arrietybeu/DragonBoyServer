package nro.service.model.npc.handler;

import nro.consts.ConstMenu;
import nro.consts.ConstNpc;
import nro.service.model.npc.ANpcHandler;
import nro.service.model.npc.Npc;
import nro.service.model.player.Player;
import nro.service.core.npc.NpcService;

@ANpcHandler({ConstNpc.LY_TIEU_NUONG})
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
