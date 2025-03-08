package nro.service.core;

import lombok.Getter;
import nro.consts.ConstItem;
import nro.model.item.Item;
import nro.model.item.ItemOption;
import nro.model.player.Player;
import nro.model.player.PlayerInventory;
import nro.model.player.PlayerPoints;
import nro.server.LogServer;
import nro.service.NpcService;
import nro.service.PlayerService;
import nro.service.Service;

public class UseItem {

    @Getter
    private static final UseItem instance = new UseItem();

    public void useItem(Player player, int index, int template) {
        if (index == -1) {
            this.eatPea(player, null, template);
            return;
        }
        PlayerInventory playerInventory = player.getPlayerInventory();
        Item item = playerInventory.getItemsBag().get(index);
        if (item == null || item.getTemplate() == null) {
            NpcService.getInstance().sendNpcTalkUI(player, 5,
                    "Có lỗi xảy ra vui lòng thử lại sau.", -1);
            return;
        }
        switch (item.getTemplate().type()) {
            case ConstItem.TYPE_PEA: {
                this.eatPea(player, item);
                break;
            }
            case ConstItem.TYPE_MOUNT:
            case ConstItem.TYPE_MOUNT_VIP: {
                playerInventory.equipItemFromBag(index);
            }
        }
    }

    private void eatPea(Player player, Item item, int... itemId) {
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
                Service.getInstance().sendChatGlobal(player.getSession(), null, "Đã xảy ra lỗi!", false);
                return;
            }

            PlayerPoints points = player.getPlayerPoints();

            long currentMp = points.getCurrentMP();
            long currentHP = points.getCurrentHP();

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

            long newMP = currentMp + hpKiHoiPhuc;
            long newHP = points.getCurrentHP() + hpKiHoiPhuc;

            if (currentMp >= points.getMaxMP()) {
                points.setCurrentHp(newHP);
                playerService.sendHpForPlayer(player);
            } else if (currentHP >= points.getMaxHP()) {
                points.setCurrentMp(newMP);
                playerService.sendMpForPlayer(player);
            } else {
                points.setCurrentMp(newMP);
                points.setCurrentHp(newHP);
                playerService.sendHpForPlayer(player);
                playerService.sendMpForPlayer(player);
            }

            player.getPlayerInventory().subQuantityItemsBag(pea, 1);
            player.getPlayerMagicTree().setLastUsePea(time);
        } catch (Exception ex) {
            LogServer.LogException("eatPea: " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }

    private Item getItem(Player player, Item item, int[] itemId) {
        Item pea = null;
        if (item != null) {
            pea = item;
            return pea;
        }
        if (itemId != null && itemId.length > 0) {
            for (int id : itemId) {
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
