package nro.model.npc;

import lombok.Getter;
import lombok.Setter;
import nro.model.map.GameMap;
import nro.model.player.Player;
import nro.server.manager.MapManager;
import nro.service.NpcService;
import nro.service.Service;

@Getter
@Setter
public class Npc implements INpcAction {

    private int mapId;
    private GameMap map;

    private int status;
    private int avatar;

    private int x;
    private int y;

    private int tempId;

    public Npc(int tempId, int status, int mapId, int cx, int cy, int avartar) {
        this.tempId = tempId;
        this.status = status;
        this.mapId = mapId;
        this.x = cx;
        this.y = cy;
        this.avatar = avartar;
        this.map = MapManager.getInstance().findMapById(mapId);
    }

    public Npc(int npcId, int x, int y, int status, int avatar) {
        this.tempId = npcId;
        this.x = x;
        this.y = y;
        this.status = status;
        this.avatar = avatar;
    }

    @Override
    public void openMenu(Player player) {
        NpcService.getInstance().sendNpcChatAllPlayerInArea(player, this, "Xin chào");
        Service.getInstance().sendChatGlobal(player.getSession(), null, "hihi", false);
    }

    @Override
    public void openUIConFirm(Player player, int select) {
    }

}
