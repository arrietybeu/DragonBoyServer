package nro.server.model.npc.datas;

import nro.server.consts.ConstNpc;
import nro.server.model.npc.ANpcData;
import nro.server.model.npc.Npc;
import nro.server.network.nro.NroConnection;
import nro.server.services.player.InventoryService;

/**
 * @author Arriety
 */
@ANpcData(value = {ConstNpc.RUONG_DO})
public class RuongDo extends Npc {

    public RuongDo(int id, int status, int mapID, int x, int y, int avatarId) {
        super(id, status, mapID, x, y, avatarId);
    }

    @Override
    public void openUIMenu(NroConnection client) {
        // FIXME CHECK CO DANG GIAO DỊCH KHONG || CHECK MAP DANG DUNG O DAU
        InventoryService.openInventoryBox(client);
    }

    @Override
    public void openUIConfirm(NroConnection client, int select) {
    }

}
