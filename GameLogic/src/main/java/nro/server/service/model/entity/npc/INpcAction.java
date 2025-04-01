package nro.server.service.model.entity.npc;

import nro.server.service.model.entity.player.Player;

public interface INpcAction {

    void openMenu(Player player);

    void openUIConFirm(Player player, int select);

}
