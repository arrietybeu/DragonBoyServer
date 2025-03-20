package nro.service.core.usage;

import nro.service.model.item.Item;
import nro.service.model.player.Player;

public interface IItemHandler {

    void use(Player player, Item item, int... id);

}
