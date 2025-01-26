package nro.model.item;

public class ItemOption {

    public ItemOptionTemplate optionTemplate;
    public int param;

    public ItemOption(ItemOption io) {
        this.param = io.param;
        this.optionTemplate = io.optionTemplate;
    }
}
