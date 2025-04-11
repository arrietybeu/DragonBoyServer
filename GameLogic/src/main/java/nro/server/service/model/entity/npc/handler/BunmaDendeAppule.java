package nro.server.service.model.entity.npc.handler;

import nro.consts.*;
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

        String npcSay = "";
        if (this.isValideOpenMenuForNpc(player.getGender())) {
            npcSay = "Cậu không thể mở cửa hàng ở đây";
            NpcService.getInstance().createMenu(player, this.getTempId(), ConstMenu.BASE_MENU, npcSay, "Cửa\nhàng");
            return;
        }
        npcSay = "Cậu cần trang bị gì cứ đến chỗ tôi nhé";
        NpcService.getInstance().createMenu(player, this.getTempId(), ConstMenu.BASE_MENU, npcSay, "Cửa\nhàng");
    }

    @Override
    public void openUIConfirm(Player player, int select) {
        if (player.getPlayerState().isBaseMenu()) {
            switch (player.getArea().getMap().getId()) {
                case ConstMap.LANG_ARU -> ShopService.getInstance().sendNornalShop(player, ConstShop.SHOP_BUNMA);
                case ConstMap.LANG_MOORI -> ShopService.getInstance().sendNornalShop(player, ConstShop.SHOP_DENDE);
                case ConstMap.LANG_KAKAROT -> ShopService.getInstance().sendNornalShop(player, ConstShop.SHOP_APPULE);
            }
        }
    }

    private boolean isValideOpenMenuForNpc(int gender) {
        return switch (gender) {
            case ConstPlayer.TRAI_DAT -> this.getTempId() != ConstNpc.BUNMA;
            case ConstPlayer.NAMEC -> this.getTempId() != ConstNpc.DENDE;
            case ConstPlayer.XAYDA -> this.getTempId() != ConstNpc.APPULE;
            default -> false;
        };
    }
}
