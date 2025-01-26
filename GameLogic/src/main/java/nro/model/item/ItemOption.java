package nro.model.item;

import lombok.Data;

@Data
public class ItemOption {

    public ItemOptionTemplate optionTemplate;
    public int param;

    public ItemOption(ItemOption io) {
        this.param = io.param;
        this.optionTemplate = io.optionTemplate;
    }
}
