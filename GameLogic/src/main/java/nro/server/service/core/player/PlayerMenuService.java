package nro.server.service.core.player;

import nro.consts.ConstMsgSubCommand;
import nro.server.system.LogServer;
import nro.server.network.Message;

public class PlayerMenuService {

    private static final class SingletonHolder {
        private static final PlayerMenuService instance = new PlayerMenuService();
    }

    public static PlayerMenuService getInstance() {
        return PlayerMenuService.SingletonHolder.instance;
    }

    public void showListMenuPlayer() {
        try (Message message = new Message(-30)) {
            message.writer().writeByte(ConstMsgSubCommand.PLAYER_MENU_LIST);
        } catch (Exception ex) {
            LogServer.LogException("showListMenuPlayer: " + ex.getMessage(), ex);
        }
    }
}
