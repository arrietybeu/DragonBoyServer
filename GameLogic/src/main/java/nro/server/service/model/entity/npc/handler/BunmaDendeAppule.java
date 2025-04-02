package nro.server.service.model.entity.npc.handler;

import nro.consts.ConstMap;
import nro.consts.ConstMenu;
import nro.consts.ConstNpc;
import nro.consts.ConstShop;
import nro.server.service.core.economy.ShopService;
import nro.server.service.model.entity.npc.ANpcHandler;
import nro.server.service.model.entity.npc.Npc;
import nro.server.service.model.entity.player.Player;
import nro.server.service.core.npc.NpcService;

@ANpcHandler({ConstNpc.BUNMA, ConstNpc.DENDE, ConstNpc.APPULE})
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
    public void openUIConfirm(Player player, int select) {
        if (player.getPlayerStatus().isBaseMenu()) {
            switch (player.getArea().getMap().getId()) {
                case ConstMap.LANG_ARU -> ShopService.getInstance().sendNornalShop(player, 0);
                case ConstMap.LANG_MOORI -> ShopService.getInstance().sendNornalShop(player, 1);
                case ConstMap.LANG_KAKAROT -> ShopService.getInstance().sendNornalShop(player, ConstShop.SHOP_APPULE);
            }
        }
    }

}
