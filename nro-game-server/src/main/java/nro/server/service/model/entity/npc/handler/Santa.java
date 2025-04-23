package nro.server.service.model.entity.npc.handler;

import nro.consts.ConstMenu;
import nro.consts.ConstNpc;
import nro.server.service.core.npc.NpcService;
import nro.server.service.model.entity.npc.ANpcHandler;
import nro.server.service.model.entity.npc.Npc;
import nro.server.service.model.entity.player.Player;

@ANpcHandler({ConstNpc.SANTA})
public class Santa extends Npc {

    public Santa(int tempId, int status, int mapId, int cx, int cy, int avatar) {
        super(tempId, status, mapId, cx, cy, avatar);
    }

    @Override
    public void openMenu(Player player) {
        String npcSay = "Xin chào, ta có một số vật phẩm đặc biệt cậu có muốn xem không?";
        NpcService.getInstance().createMenu(
                player, this.getTempId(), ConstMenu.BASE_MENU, npcSay,
                "Cửa\nhàng",
                "Mở rộng\nHành trang\nRương đồ",
                "Nhập mã\nquà tặng",
                "Cửa hàng\nHạn sử dụng",
                "Tiệm\nHớt tóc",
                "Danh\nhiệu"
        );
    }

    @Override
    public void openUIConfirm(Player player, int select) {

    }
}
