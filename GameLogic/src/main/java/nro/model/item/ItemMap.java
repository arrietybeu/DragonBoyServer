package nro.model.item;

import lombok.Getter;
import nro.model.map.areas.Area;
import nro.server.LogServer;
import nro.service.ItemService;

@Getter
@SuppressWarnings("ALL")
public class ItemMap {

    private Item item;
    private Area area;

    private final int playerId;
    private final short itemMapID;
    private final int x;
    private final int y;
    private final short range;
    private final long lastTimeRemoveItem;

    public ItemMap(Area area, int playerId, Item item, int x, int y, int range) {
        this.area = area;
        this.itemMapID = area.increaseItemMapID();
        this.playerId = playerId;
        this.item = item;
        this.x = x;
        this.y = y;
        this.lastTimeRemoveItem = System.currentTimeMillis();
        this.range = (short) range;
        area.addItemMap(this);
    }

    @Override
    public String toString() {
        return "ItemMap{" +
                "playerId=" + playerId +
                ", itemMapID=" + itemMapID +
                ", x=" + x +
                ", y=" + y +
                ", range=" + range +
                ", area=" + area.getId() +
                ", lastTimeRemoveItem=" + lastTimeRemoveItem +
                '}';
    }
}
