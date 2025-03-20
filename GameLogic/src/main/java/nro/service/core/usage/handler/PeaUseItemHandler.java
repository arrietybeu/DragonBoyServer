package nro.service.core.usage.handler;

import nro.consts.ConstItem;
import nro.server.LogServer;
import nro.service.core.player.PlayerService;
import nro.service.core.system.ServerService;
import nro.service.core.usage.AUseItemHandler;
import nro.service.core.usage.IUseItemHandler;
import nro.service.model.item.Item;
import nro.service.model.player.Player;
import nro.service.model.player.PlayerPoints;
import nro.service.model.template.item.ItemOption;

@AUseItemHandler({ConstItem.TYPE_PEA})
public class PeaUseItemHandler implements IUseItemHandler {

    @Override
    public void use(Player player, int type, int index, Item item, int... itemId) {
        long time = System.currentTimeMillis();

        if (player.getPlayerMagicTree().getLastUsePea() + 10000 > time) {
            // System.out.println("Còn thời gian chờ:
            // " + (player.getPlayerMagicTree().getLastUsePea() + 10000 - time) + "ms");
            return;
        }

        try {
            Item pea = this.getItem(player, item, itemId);
            PlayerService playerService = PlayerService.getInstance();
            if (pea == null) {
                ServerService.getInstance().sendChatGlobal(player.getSession(), null, "Đã xảy ra lỗi!", false);
                return;
            }

            PlayerPoints points = player.getPlayerPoints();

            long currentHP = points.getCurrentHP();
            long currentMp = points.getCurrentMP();

            if (currentHP >= points.getMaxHP() && currentMp >= points.getMaxMP()) {
                playerService.sendCurrencyHpMp(player);
                return;
            }

            int hpKiHoiPhuc = 0;
            for (ItemOption io : pea.getItemOptions()) {
                if (io.getId() == 2) {
                    hpKiHoiPhuc = io.getParam() * 1000;
                    break;
                }
                if (io.getId() == 48) {
                    hpKiHoiPhuc = io.getParam();
                    break;
                }
            }

            long newHP = currentHP + hpKiHoiPhuc;
            long newMP = currentMp + hpKiHoiPhuc;

            if (currentMp < points.getMaxMP()) {
                points.setCurrentMp(newMP);
                playerService.sendMpForPlayer(player);
            } else {
                points.setCurrentHp(newHP);
                playerService.sendHpForPlayer(player);
            }

            player.getPlayerInventory().subQuantityItemsBag(pea, 1);
            player.getPlayerMagicTree().setLastUsePea(time);
        } catch (Exception ex) {
            LogServer.LogException("eatPea: " + ex.getMessage(), ex);
        }
    }

    private Item getItem(Player player, Item item, int[] itemId) {
        Item pea = null;
        if (item != null) {
            pea = item;
            return pea;
        }
        if (itemId != null) {
            for (int id : itemId) {
                // auto close
                Item it = player.getPlayerInventory().findItemInBag(id);
                if (it.getTemplate().id() == id) {
                    pea = it;
                    return pea;
                }
            }
        }
        return pea;
    }

}
