package nro.model.item;

import lombok.Getter;

@Getter
public class ItemMap {

    private int playerId;
    private short itemMapID;
    private int x;
    private int y;
    private short range;
    private ItemTemplate itemTemplate;

    public void update() {
    }
}
