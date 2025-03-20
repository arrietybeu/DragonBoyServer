package nro.service.model.template.item;

import nro.service.model.item.Item;

public record Flag(int id, int itemId, int icon, Item itemFlagBag) {
}
