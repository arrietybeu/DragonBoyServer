package nro.service.core.usage;

import lombok.Getter;
import nro.consts.ConstItem;
import nro.service.model.item.Item;
import nro.service.model.template.item.ItemOption;
import nro.service.model.player.Player;
import nro.service.model.player.PlayerInventory;
import nro.service.model.player.PlayerPoints;
import nro.server.LogServer;
import nro.service.core.npc.NpcService;
import nro.service.core.player.PlayerService;
import nro.service.core.system.ServerService;

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
        LogServer.DebugLogic("item name: " + item.getTemplate().name() + " type: " + item.getTemplate().type());
        switch (item.getTemplate().type()) {
            case ConstItem.TYPE_PEA -> this.eatPea(player, item);
            case ConstItem.TYPE_MOUNT_VIP, ConstItem.TYPE_MOUNT -> playerInventory.equipItemFromBag(index);
            case ConstItem.TYPE_LEARN_SKILL -> {
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
