package nro.server.service.model.entity.npc;

import lombok.Getter;
import lombok.Setter;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.map.GameMap;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;
import nro.server.manager.MapManager;
import nro.server.service.core.npc.NpcService;
import nro.server.service.core.system.ServerService;

@Getter
@Setter
public abstract class Npc {

    private int mapId;
    private final GameMap map;
    private int status;
    private int avatar;
    private int x;
    private int y;
    private int tempId;
    private boolean isHide;

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
            return this.getClass().getDeclaredConstructor(int.class, int.class, int.class, int.class, int.class, int.class).newInstance(npcId, status, mapId, x, y, avatar);
        } catch (Exception e) {
            LogServer.LogException("Error cloning NPC: " + e.getMessage(), e);
        }
        return null;
    }

    public void turnOnHideNpc(Entity entity, boolean isHide) {
        this.isHide = isHide;
        NpcService.getInstance().sendHideNpcInArea(entity, this);
    }

    public abstract void openMenu(Player player);

    public abstract void openUIConfirm(Player player, int select);

}