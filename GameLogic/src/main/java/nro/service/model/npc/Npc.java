package nro.service.model.npc;

import lombok.Getter;
import lombok.Setter;
import nro.service.model.map.GameMap;
import nro.service.model.entity.player.Player;
import nro.server.system.LogServer;
import nro.server.manager.MapManager;
import nro.service.core.npc.NpcService;
import nro.service.core.system.ServerService;

@Getter
@Setter
public class Npc implements INpcAction {

    private int mapId;
    private final GameMap map;
    private int status;
    private int avatar;
    private int x;
    private int y;
    private int tempId;

    public Npc(int tempId, int status, int mapId, int cx, int cy, int avatar) {
        this.tempId = tempId;
        this.status = status;
        this.mapId = mapId;
        this.x = cx;
        this.y = cy;
        this.avatar = avatar;
        this.map = MapManager.getInstance().findMapById(mapId);
    }

    public Npc cloneNpc(int npcId, int status, int mapId, int x, int y, int avatar) {
        try {
            return this.getClass().getDeclaredConstructor(int.class, int.class, int.class, int.class, int.class, int.class)
                    .newInstance(npcId, status, mapId, x, y, avatar);
        } catch (Exception e) {
            LogServer.LogException("Error cloning NPC: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void openMenu(Player player) {
        ServerService.getInstance().sendHideWaitDialog(player);
        NpcService.getInstance().sendNpcChatAllPlayerInArea(player, this, "Xin chào");
    }

    @Override
    public void openUIConFirm(Player player, int select) {
    }

    public void update() {
    }

}