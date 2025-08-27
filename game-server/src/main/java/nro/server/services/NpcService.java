package nro.server.services;

import nro.server.consts.ConstNpc;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.npc.Npc;
import nro.server.model.npc.NpcFactory;
import nro.server.model.world.World;
import nro.server.network.nro.NroConnection;

/**
 * @author Arriety
 */
public class NpcService {


    public void openMenuNpc(NroConnection client, int npcId) {
        var positions = client.getEntity().getComponent(PositionComponent.class);
        var currentMap = World.getInstance().getMap(positions.mapId);

        Npc npc = currentMap.getNpcById(npcId);

        if (npc == null) {
            if (npcId == ConstNpc.LY_TIEU_NUONG) {
                Npc lyTieuNuong = NpcFactory.getNpc(ConstNpc.LY_TIEU_NUONG);
                if (lyTieuNuong != null) {
                    lyTieuNuong.openUIMenu(client);
                } else {
//                    NpcService.getInstance().sendNpcTalkUI(player, 5, "Có lỗi xảy ra vui lòng thử lại sau.", -1);
                }
            } else {
//                NpcService.getInstance().sendNpcTalkUI(player, 5, "Có lỗi xảy ra vui lòng thử lại sau.", -1);
            }
            return;
        }
        if (npc.isHide()) return;
        npc.openUIMenu(client);
    }

    public void sendNpcTalkUI(NroConnection client, int npcId, String message, int avatarId) {
    }

    private static final class SingletonHolder {
        private static final NpcService instance = new NpcService();
    }

    public static NpcService getInstance() {
        return NpcService.SingletonHolder.instance;
    }

}
