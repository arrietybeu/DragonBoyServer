package nro.server.service.model.item;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ItemShop extends Item {


    private int tabId;
    private int indexItem;

    private int sellGold;
    private int sellGem;
    private int costSellSpec;

    private boolean isNewItem;
    private boolean isSell;

    private byte typeSell;
    private short iconSpec;


}
