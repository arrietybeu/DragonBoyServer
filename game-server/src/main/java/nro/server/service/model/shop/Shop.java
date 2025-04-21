package nro.server.service.model.shop;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class Shop {

    private int id;
    private int npcId;
    private byte typeShop;

    private List<TabShop> tabShops;

    public Shop() {
        this.tabShops = new ArrayList<>();
    }

}
