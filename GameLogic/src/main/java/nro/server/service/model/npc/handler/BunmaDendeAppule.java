package nro.server.service.model.npc.handler;

import nro.consts.ConstMenu;
import nro.consts.ConstNpc;
import nro.server.service.model.npc.ANpcHandler;
import nro.server.service.model.npc.Npc;
import nro.server.service.model.entity.player.Player;
import nro.server.service.core.npc.NpcService;

@ANpcHandler({ConstNpc.BUNMA})
public class BunmaDendeAppule extends Npc {

    public BunmaDendeAppule(int tempId, int status, int mapId, int cx, int cy, int avartar) {
        super(tempId, status, mapId, cx, cy, avartar);
    }

    @Override
    public void openMenu(Player player) {
        if (player.getPlayerTask().checkDoneTaskTalkNpc(this)) return;
        String npcSay = "Cậu cần trang bị gì cứ đến chỗ tôi nhé";
        NpcService.getInstance().createMenu(player, this.getTempId(), ConstMenu.BASE_MENU, npcSay, "Cửa\nhàng");
    }

    @Override
    public void openUIConFirm(Player player, int select) {
        String beo = "My spirit is not stable";
    }

}
