package nro.service.model.template.item;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemOption {

    private int id;
    private int param;
    private short type;

    public ItemOption(int id, int param) {
        this.id = id;
        this.param = param;
    }

    // constructor Default option
    public ItemOption() {
        this.id = 73;
        this.param = 0;
    }

}
