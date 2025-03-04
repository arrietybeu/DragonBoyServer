package nro.model.item;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemOption {

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
