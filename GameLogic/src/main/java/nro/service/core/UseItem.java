package nro.service.core;

import lombok.Getter;
import nro.model.item.Item;
import nro.model.item.ItemOption;
import nro.model.player.Player;
import nro.service.NpcService;

public class UseItem {

    @Getter
    private static final UseItem instance = new UseItem();

    public void useItem(Player player, int index) {
        if (index == -1) {
            this.eatPea(player);
            return;
        }
        Item item = player.getPlayerInventory().getItemsBag().get(index);
        if (item == null || item.getTemplate() == null) {
            NpcService.getInstance().sendNpcTalkUI(player, 5, "Có lỗi xảy ra vui lòng thử lại sau.", -1);
            return;
        }
        switch (item.getTemplate().type()) {
            case 6: {
                this.eatPea(player);
                break;
            }
        }
    }

    private void eatPea(Player player) {
        for (Item item : player.getPlayerInventory().getItemsBag()) {
            if (item.getTemplate() == null) {
                continue;
            }
            if (item.getTemplate().type() == 6) {
                int hpKiHoiPhuc = 0;
                for (ItemOption io : item.getItemOptions()) {
                    if (io.id == 2) {
                        hpKiHoiPhuc = io.param * 1000;
                        break;
                    }
                    if (io.id == 48) {
                        hpKiHoiPhuc = io.param;
                        break;
                    }
                }
                // send gi do
            }
        }
    }

}
