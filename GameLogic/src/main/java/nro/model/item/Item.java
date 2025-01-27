package nro.model.item;

import lombok.Data;

import java.util.List;

@Data
public class Item {
    private int quantity;
    private long createTime;
    private ItemTemplate template;
    private List<ItemOption> itemOptions;
    private String info;
    private String content;
}
