package nro.server.service.model.entity.npc.handler;

import nro.consts.ConstMenu;
import nro.consts.ConstNpc;
import nro.server.service.core.npc.NpcService;
import nro.server.service.model.entity.npc.ANpcHandler;
import nro.server.service.model.entity.npc.Npc;
import nro.server.service.model.entity.player.Player;

@ANpcHandler({ConstNpc.QUY_LAO_KAME})
public class QuyLaoKame extends Npc {

    public QuyLaoKame(int tempId, int status, int mapId, int cx, int cy, int avatar) {
        super(tempId, status, mapId, cx, cy, avatar);
    }

    @Override
    public void openMenu(Player player) {
        if (player.getPlayerTask().checkDoneTaskTalkNpc(this)) return;
        String npcSay = "Ngươi muốn có thêm ngọc thì chịu khó làm vài nhiệm vụ sẽ được ngọc thưởng";
        NpcService.getInstance().createMenu(
                player, this.getTempId(), ConstMenu.BASE_MENU, npcSay,
                "Nạp Ngọc",
                "Nhận ngọc\nMiễn phí",
                "Nhiệm vụ\nhằng ngày"
        );
    }

    @Override
    public void openUIConfirm(Player player, int select) {
    }
}
