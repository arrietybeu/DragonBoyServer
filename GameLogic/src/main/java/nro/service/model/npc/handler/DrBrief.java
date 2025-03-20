package nro.service.model.npc.handler;

import nro.consts.ConstMap;
import nro.consts.ConstMenu;
import nro.consts.ConstNpc;
import nro.service.model.npc.ANpcHandler;
import nro.service.model.npc.Npc;
import nro.service.model.player.Player;
import nro.service.core.map.AreaService;
import nro.service.core.npc.NpcService;

@ANpcHandler({ConstNpc.DR_BRIEF})
public class DrBrief extends Npc {

    public DrBrief(int tempId, int status, int mapId, int cx, int cy, int avatar) {
        super(tempId, status, mapId, cx, cy, avatar);
    }

    @Override
    public void openMenu(Player player) {
        String npcSay = "Tàu Vũ Trụ của ta có thể dưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?";
        String[] npcSayArray = new String[]{"Đến\nNamếc", "Đến\nXayda", "Siêu thị"};

        NpcService.getInstance().createMenu(player, this.getTempId(), ConstMenu.BASE_MENU, npcSay, npcSayArray);
    }

    @Override
    public void openUIConFirm(Player player, int select) {
        AreaService areaService = AreaService.getInstance();
        if (player.getPlayerStatus().isBaseMenu()) {
            switch (select) {
                case 0 -> areaService.changerMapByShip(player, ConstMap.TRAM_TAU_VU_TRU_NAMEC, 1);
                case 1 -> areaService.changerMapByShip(player, ConstMap.TRAM_TAU_VU_TRU_XAYDA, 1);
            }
        }
    }

}
