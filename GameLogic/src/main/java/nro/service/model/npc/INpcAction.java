package nro.service.model.npc;

import nro.service.model.entity.player.Player;

public interface INpcAction {

    void openMenu(Player player);

    void openUIConFirm(Player player, int select);

}
