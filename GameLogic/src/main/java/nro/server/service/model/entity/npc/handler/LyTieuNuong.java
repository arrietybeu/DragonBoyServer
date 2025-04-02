package nro.server.service.model.entity.npc.handler;

import nro.consts.ConstMenu;
import nro.consts.ConstNpc;
import nro.server.service.model.entity.npc.ANpcHandler;
import nro.server.service.model.entity.npc.Npc;
import nro.server.service.model.entity.player.Player;
import nro.server.service.core.npc.NpcService;

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
    public void openUIConfirm(Player player, int select) {
    }

}
