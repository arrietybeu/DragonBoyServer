package nro.server.service.model.item;

import lombok.Getter;
import lombok.Setter;
import nro.server.realtime.system.item.ItemMapSystem;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.map.areas.Area;

@Getter

@SuppressWarnings("ALL")
public class ItemMap extends Entity {

    private Item item;
    private Area area;

    @Setter
    private int playerId;
    private final short range;
    private final long lastTimeRemoveItem;

    public ItemMap(Area area, int itemMapId, int playerId, Item item, int x, int y, int range, boolean isDrop) throws IllegalArgumentException {
        this.area = area;
        this.id = itemMapId;
        this.playerId = playerId;
        this.item = item;
        this.x = (short) x;
        this.y = (short) y;
        this.lastTimeRemoveItem = System.currentTimeMillis();
        this.range = (short) range;
        if (isDrop) {
            area.addItemMap(this);
            ItemMapSystem.getInstance().register(area);
        }
        if (item.getTemplate() == null) {
            throw new IllegalArgumentException("item template is null");
        }
    }

    @Override
    public String toString() {
        return "ItemMap{" +
                "playerId=" + playerId +
                ", itemMapID=" + id +
                ", x=" + x +
                ", y=" + y +
                ", range=" + range +
                ", area=" + area.getId() +
                ", lastTimeRemoveItem=" + lastTimeRemoveItem +
                '}';
    }

    @Override
    public synchronized long handleAttack(Entity entityAttack, int type, long damage) {
        return 0;
    }

    @Override
    protected void onDie(Entity killer) {
    }

    @Override
    public void dispose() {
    }
}
