package nro.model.player;

import lombok.Data;
import nro.model.item.Item;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlayerInventory {

    private final Player player;
    public final List<Item> itemsBody;
    public final List<Item> itemsBag;
    public final List<Item> itemsBox;

    public PlayerInventory(Player player) {
        this.player = player;
        this.itemsBody = new ArrayList<>();
        this.itemsBag = new ArrayList<>();
        this.itemsBox = new ArrayList<>();
    }
}
