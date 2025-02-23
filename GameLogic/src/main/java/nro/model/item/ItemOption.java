package nro.model.item;

import lombok.Getter;
import lombok.Setter;
import nro.server.manager.ItemManager;

@Getter
@Setter
public class ItemOption {

    private final static ItemManager itemManager = ItemManager.getInstance();

    private int id;
    private int param;

    public ItemOption(int id, int param) {
        this.id = id;
        this.param = param;
    }

    public ItemOption() {
        this.id = 73;
        this.param = 0;
    }

}
