package nro.server.service.model.entity.player;

import nro.consts.ConstItem;
import nro.server.system.LogServer;
import nro.server.service.model.entity.Fashion;
import nro.server.service.model.item.Item;

import java.util.List;

public class PlayerFashion extends Fashion {

    private final Player player;

    public PlayerFashion(Player player) {
        this.player = player;
    }

    @Override
    public void updateFashion() {
        try {
            PlayerInventory inventory = this.player.getPlayerInventory();
            List<Item> itemsBody = inventory != null ? inventory.getItemsBody() : null;

            // reset fashion
            this.head = this.body = this.leg = this.mount = this.flagBag = -1;

            int indexCaiTrang = ConstItem.TYPE_CAI_TRANG_OR_AVATAR;
            int indexAo = ConstItem.TYPE_AO;
            int indexQuan = ConstItem.TYPE_QUAN;

            if (itemsBody != null) {
                // Head
                if (itemsBody.get(indexCaiTrang) != null && itemsBody.get(indexCaiTrang).getTemplate() != null) {
                    var template = itemsBody.get(indexCaiTrang).getTemplate();
                    short h = template.head();
                    this.head = (h != -1) ? h : template.part();

                }
                // set head default
                if (this.head == -1) {
                    this.head = this.headDefault;
                }


                // Ưu tiên body từ cải trang
                if (itemsBody.get(indexCaiTrang) != null && itemsBody.get(indexCaiTrang).getTemplate() != null) {
                    short b = itemsBody.get(indexCaiTrang).getTemplate().body();
                    if (b != -1) {
                        this.body = b;
                    }
                }

                // Fallback: lấy từ item áo thật sự nếu chưa có
                if (this.body == -1 && itemsBody.size() > indexAo && itemsBody.get(indexAo) != null
                        && itemsBody.get(indexAo).getTemplate() != null) {
                    this.body = itemsBody.get(indexAo).getTemplate().body();
                }

                // Leg
                if (itemsBody.get(indexCaiTrang) != null && itemsBody.get(indexCaiTrang).getTemplate() != null) {
                    short l = itemsBody.get(indexCaiTrang).getTemplate().leg();
                    if (l != -1) this.leg = l;
                }

                if (this.leg == -1 && itemsBody.get(indexQuan) != null && itemsBody.get(indexQuan).getTemplate() != null) {
                    this.leg = itemsBody.get(indexQuan).getTemplate().leg();
                }

                // Mount
                if (itemsBody.size() > 9 && itemsBody.get(9) != null && itemsBody.get(9).getTemplate() != null) {
                    this.mount = itemsBody.get(9).getTemplate().id();
                }

                // FlagBag
                if (itemsBody.size() > 8 && itemsBody.get(8) != null && itemsBody.get(8).getTemplate() != null) {
                    this.flagBag = itemsBody.get(8).getTemplate().part();
                }
            }

            var taskMain = this.player.getPlayerTask().getTaskMain();
            if (this.flagBag == -1 && taskMain.getId() == 3 && taskMain.getIndex() == 2) {
                this.flagBag = 28;
            }

        } catch (Exception e) {
            LogServer.LogException("PlayerFashion.updateFashion: " + e.getMessage(), e);
        }
    }

}
