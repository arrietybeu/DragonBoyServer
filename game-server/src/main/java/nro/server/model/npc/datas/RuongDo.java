package nro.server.model.npc.datas;

import nro.server.consts.ConstNpc;
import nro.server.model.ecs.component.player.InventoryComponent;
import nro.server.model.npc.ANpcData;
import nro.server.model.npc.Npc;
import nro.server.network.nro.NroConnection;

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

        InventoryComponent inventoryComponent = client.getEntity().getComponent(InventoryComponent.class);

    }

    @Override
    public void openUIConfirm(NroConnection client, int select) {

    }

}
