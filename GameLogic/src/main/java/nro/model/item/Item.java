package nro.model.item;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Item {
    private int quantity;
    private long createTime;
    private ItemTemplate template;
    private List<ItemOption> itemOptions = new ArrayList<>();
    private String info;
    private String content;
}
