package nro.server.service.core.usage;

import nro.server.service.model.item.Item;
import nro.server.service.model.entity.player.Player;

public interface IUseItemHandler {

    void use(Player player, int type, int index, Item item, int... id);

}
