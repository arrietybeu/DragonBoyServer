package nro.model.npc.type;

import nro.consts.ConstMap;
import nro.consts.ConstMenu;
import nro.consts.ConstNpc;
import nro.model.npc.ANpcHandler;
import nro.model.npc.Npc;
import nro.model.player.Player;
import nro.service.AreaService;
import nro.service.NpcService;

@ANpcHandler({ConstNpc.CARGO})
public class Cargo extends Npc {

    public Cargo(int tempId, int status, int mapId, int cx, int cy, int avatar) {
        super(tempId, status, mapId, cx, cy, avatar);
    }

    @Override
    public void openMenu(Player player) {
        String npcSay = "Tàu vũ trụ Namếc tuy cũ nhưng tốc độ không hè kém bất kỳ loại tàu nào khác. Cậu muốn đi đâu?";
        String[] npcSayArray = new String[]{"Đến\nTrái Đất", "Đến\nXayda"};

        NpcService.getInstance().createMenu(player, this.getTempId(), ConstMenu.BASE_MENU, npcSay, npcSayArray);
    }

    @Override
    public void openUIConFirm(Player player, int select) {
        AreaService areaService = AreaService.getInstance();
        if (player.getPlayerStatus().isBaseMenu()) {
            switch (select) {
                case 0 -> areaService.changerMapByShip(player, ConstMap.TRAM_TAU_VU_TRU_TRAI_DAT, 1);
                case 1 -> areaService.changerMapByShip(player, ConstMap.TRAM_TAU_VU_TRU_XAYDA, 1);
            }
        }
    }

}
