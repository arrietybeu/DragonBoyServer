package nro.model.player;

import lombok.Getter;
import lombok.Setter;
import nro.model.item.Item;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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

    @Override
    public String toString() {
        return "PlayerInventory{" +
                "player=" + player +
                ", itemsBody=" + itemsBody +
                ", itemsBag=" + itemsBag +
                ", itemsBox=" + itemsBox +
                '}';
    }
}
