package nro.server.service.model.template.item;

import nro.server.service.model.item.Item;

public record Flag(int id, int itemId, int icon, Item itemFlagBag) {
}
