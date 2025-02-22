package nro.model.npc.type;

import nro.consts.ConstMenu;
import nro.consts.ConstNpc;
import nro.model.npc.ANpcHandler;
import nro.model.npc.Npc;
import nro.model.player.Player;
import nro.model.player.PlayerMagicTree;
import nro.model.player.PlayerStatus;
import nro.service.NpcService;

import java.util.ArrayList;
import java.util.List;

@ANpcHandler({ConstNpc.DAU_THAN})
public class DauThan extends Npc {

    public DauThan(int tempId, int status, int mapId, int cx, int cy, int avatar) {
        super(tempId, status, mapId, cx, cy, avatar);
    }

    @Override
    public void openMenu(Player player) {
        PlayerMagicTree playerMagicTree = player.getPlayerMagicTree();
        PlayerStatus playerStatus = player.getPlayerStatus();
        List<String> menu = new ArrayList<>();

        if (playerMagicTree.isUpgrade()) {
            menu.add("Nâng cấp\nnhanh");
            menu.add("Hủy\nnâng cấp");
            playerStatus.setIndexMenu(ConstMenu.MENU_MAGIC_TREE_UPGRADE);
        } else {
            menu.add("Thu\nhoạch");
            if (playerMagicTree.getLevel() < 10) {
                menu.add(playerMagicTree.getTextUpgrade());
            }
            if (playerMagicTree.getCurrPeas() < playerMagicTree.getMaxPea()) {
                menu.add("Kết hạt\nnhanh");
            }
        }
        NpcService.getInstance().loadMagicTree(player, 1, menu);
    }

    @Override
    public void openUIConFirm(Player player, int select) {
        var indexMenu = player.getPlayerStatus().getIndexMenu();
        switch (indexMenu) {
            case ConstMenu.MENU_MAGIC_TREE_UPGRADE -> {
                switch (select) {
                    case 0 -> {
                    }
                    case 1 -> {
                    }
                }
            }
            case ConstMenu.MENU_HARVEST_PEA -> {
                switch (select) {
                    case 0 -> {
                    }
                }
            }
        }
    }
}
