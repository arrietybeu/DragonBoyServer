package nro.service.model.model.npc;

import nro.service.model.model.player.Player;

public interface INpcAction {

    void openMenu(Player player);

    void openUIConFirm(Player player, int select);

}
