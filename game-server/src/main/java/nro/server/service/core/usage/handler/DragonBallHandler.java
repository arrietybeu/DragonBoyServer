package nro.server.service.core.usage.handler;

import nro.consts.ConstItem;
import nro.server.service.core.usage.AUseItemHandler;
import nro.server.service.core.usage.IUseItemHandler;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.item.Item;

@AUseItemHandler({ConstItem.TYPE_DRAGON_BALL})
public class DragonBallHandler implements IUseItemHandler {

    @Override
    public void use(Player player, int type, int index, Item item, int... id) {

    }

}
