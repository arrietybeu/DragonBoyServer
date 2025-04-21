package nro.server.service.model.shop;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nro.server.service.model.item.ItemShop;

import java.util.Map;

@Getter
@Setter
@ToString
public class TabShop {

    private int id;
    private String name;
    private Map<Short, ItemShop> itemShopMap;
}
