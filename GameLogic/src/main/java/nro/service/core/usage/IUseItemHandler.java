package nro.service.core.usage;

import nro.service.model.item.Item;
import nro.service.model.player.Player;

public interface IUseItemHandler {

    void use(Player player, int type, int index, Item item, int... id);

}
