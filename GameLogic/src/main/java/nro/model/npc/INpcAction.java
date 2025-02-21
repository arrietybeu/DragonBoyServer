package nro.model.npc;

import nro.model.player.Player;

public interface INpcAction {

    void openMenu(Player player);
    void openUIConFirm(Player player, int select);

}
