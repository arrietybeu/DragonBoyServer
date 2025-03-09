package nro.model.item;

import lombok.Getter;
import lombok.Setter;
import nro.model.map.areas.Area;

@Getter

@SuppressWarnings("ALL")
public class ItemMap {

    private Item item;
    private Area area;

    @Setter
    private int playerId;
    private final int itemMapID;
    private final int x;
    private final int y;
    private final short range;
    private final long lastTimeRemoveItem;

    public ItemMap(Area area, int playerId, Item item, int x, int y, int range) throws IllegalArgumentException {
        this.area = area;
        this.itemMapID = area.increaseItemMapID();
        this.playerId = playerId;
        this.item = item;
        this.x = x;
        this.y = y;
        this.lastTimeRemoveItem = System.currentTimeMillis();
        this.range = (short) range;
        area.addItemMap(this);

        if (item.getTemplate() == null) {
            throw new IllegalArgumentException("item template is null");
        }
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
