package nro.server.service.model.entity.npc.handler;

import nro.consts.ConstMenu;
import nro.consts.ConstNpc;
import nro.server.service.core.system.ServerService;
import nro.server.service.model.entity.npc.ANpcHandler;
import nro.server.service.model.entity.npc.Npc;
import nro.server.service.model.entity.player.Player;

@ANpcHandler({ConstNpc.CON_MEO})
public class ConMeo extends Npc {

    public static final int TYPE_BOX = 0;
    public static final int TYPE_BAG = 1;
    public static final int TYPE_BODY = 2;
    public static final int TYPE_ALL = 3;

    public ConMeo(int tempId, int status, int mapId, int cx, int cy, int avatar) {
        super(tempId, status, mapId, cx, cy, avatar);
    }

    @Override
    public void openMenu(Player player) {
    }

    @Override
    public void openUIConfirm(Player player, int select) {
        switch (player.getPlayerContext().getIndexMenu()) {
            case ConstMenu.MENU_REMOVE_INVENTORY -> {
                ServerService serverService = ServerService.getInstance();
                switch (select) {
                    case TYPE_BOX -> {
                        player.getPlayerInventory().removeAllItemInventory(TYPE_BOX);
                        serverService.sendChatGlobal(player.getSession(), null, "Remove Bag Thành Công", false);
                    }
                    case TYPE_BAG -> {
                        player.getPlayerInventory().removeAllItemInventory(TYPE_BAG);
                        serverService.sendChatGlobal(player.getSession(), null, "Remove Bag Thành Công", false);
                    }
                    case TYPE_BODY -> {
                        player.getPlayerInventory().removeAllItemInventory(TYPE_BODY);
                        serverService.sendChatGlobal(player.getSession(), null, "Remove Bag Thành Công", false);
                    }
                    case TYPE_ALL -> {
                        player.getPlayerInventory().removeAllItemInventory(TYPE_ALL);
                        serverService.sendChatGlobal(player.getSession(), null, "Remove Bag Thành Công", false);
                    }
                }
            }
        }
    }

}
