package nro.service.model.npc.handler;

import nro.consts.ConstMenu;
import nro.consts.ConstNpc;
import nro.service.model.npc.ANpcHandler;
import nro.service.model.npc.Npc;
import nro.service.model.entity.player.Player;
import nro.service.model.entity.player.PlayerMagicTree;
import nro.service.model.entity.player.PlayerStatus;
import nro.service.core.npc.NpcService;

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

        if (player.getPlayerTask().getTaskMain().getId() <= 1) {
            menu.add("Thu\nhoạch");
            playerStatus.setIndexMenu(ConstMenu.MENU_HARVEST_PEA);
        } else if (playerMagicTree.isUpgrade()) {
            menu.add("Nâng cấp\nnhanh");
            menu.add("Hủy\nnâng cấp\nhồi " + playerMagicTree.getGold());
            playerStatus.setIndexMenu(ConstMenu.MENU_MAGIC_TREE_UPGRADE);
        } else {
            menu.add("Thu\nhoạch");
            if (playerMagicTree.getLevel() < 10) {
                menu.add(playerMagicTree.getTextUpgrade());
            }
            if (playerMagicTree.getCurrPeas() < playerMagicTree.getMaxPea()) {
                menu.add("Kết hạt\nnhanh");
            }
            playerStatus.setIndexMenu(ConstMenu.MENU_HARVEST_PEA);
        }
        NpcService.getInstance().loadMagicTree(player, 1, menu);
    }

    @Override
    public void openUIConFirm(Player player, int select) {
        var indexMenu = player.getPlayerStatus().getIndexMenu();
        var playerMagicTree = player.getPlayerMagicTree();
        switch (indexMenu) {
            case ConstMenu.MENU_HARVEST_PEA: {
                switch (select) {
                    case 0: {
                        playerMagicTree.harvestPea();
                        player.getPlayerTask().checkDoneTaskConfirmMenuNpc(this.getTempId());
                        break;
                    }
                    case 1: {
                        if (playerMagicTree.getLevel() < 10) {
                            this.sendMenuUpgradeMagicTree(player);
                        } else {
                            if (playerMagicTree.getCurrPeas() < playerMagicTree.getMaxPea()) {
                                playerMagicTree.resetPea();
                            }
                        }
                        break;
                    }
                    case 2: {
                        if (playerMagicTree.getCurrPeas() < playerMagicTree.getMaxPea()) {
                            playerMagicTree.resetPea();
                        }
                        break;
                    }
                }
                break;
            }
            case ConstMenu.MENU_MAGIC_TREE_UPGRADE: {
                switch (select) {
                    case 0: {
                        player.getPlayerMagicTree().fastUpgradeMagicTree();
                        break;
                    }
                    case 1: {
                        this.sendMenuCancelUpgrade(player);
                        break;
                    }
                }
                break;
            }
            case ConstMenu.MENU_CANCEL_UPGRADE_MAGIC_TREE: {
                if (select == 0) {
                    player.getPlayerMagicTree().cancelUpgradeMagicTree();
                }
                break;
            }
            case ConstMenu.MENU_UPGRADE_MAGIC_TREE: {
                if (select == 0) {
                    player.getPlayerMagicTree().upgradeMagicTree();
                }
                break;
            }
        }
    }

    private void sendMenuCancelUpgrade(Player player) {
        NpcService.getInstance().createMenu(player, this.getTempId(), ConstMenu.MENU_CANCEL_UPGRADE_MAGIC_TREE, "Hủy nâng cấp hồi " + player.getPlayerMagicTree().getGold(), "Hủy nâng cấp");
    }

    private void sendMenuUpgradeMagicTree(Player player) {
        NpcService.getInstance().createMenu(player, this.getTempId(), ConstMenu.MENU_UPGRADE_MAGIC_TREE, "Bạn có chắc chắn nâng cấp cây đậu?", "OK", "Từ chối");

    }
}
