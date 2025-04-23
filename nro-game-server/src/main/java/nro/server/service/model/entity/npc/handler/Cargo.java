package nro.server.service.model.entity.npc.handler;

import nro.consts.ConstMap;
import nro.consts.ConstMenu;
import nro.consts.ConstNpc;
import nro.server.service.model.entity.npc.ANpcHandler;
import nro.server.service.model.entity.npc.Npc;
import nro.server.service.model.entity.player.Player;
import nro.server.service.core.map.AreaService;
import nro.server.service.core.npc.NpcService;
import nro.commons.utils.Rnd;

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
    public void openUIConfirm(Player player, int select) {
        AreaService areaService = AreaService.getInstance();
        if (player.getPlayerContext().isBaseMenu()) {
            switch (select) {
                case 0 ->
                        areaService.changerMapByShip(player, ConstMap.TRAM_TAU_VU_TRU_TRAI_DAT, Rnd.nextInt(400, 444), 5, 1);
                case 1 ->
                        areaService.changerMapByShip(player, ConstMap.TRAM_TAU_VU_TRU_XAYDA, Rnd.nextInt(400, 444), 5, 1);
            }
        }
    }

}
