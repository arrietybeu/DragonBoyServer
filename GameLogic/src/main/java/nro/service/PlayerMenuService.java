package nro.service;

import lombok.Getter;
import nro.consts.ConstMsgSubCommand;
import nro.server.LogServer;
import nro.server.network.Message;

public class PlayerMenuService {

    @Getter
    private static final PlayerMenuService instance = new PlayerMenuService();

    public void showListMenuPlayer() {
        try (Message message = new Message(-30)) {
            message.writer().writeByte(ConstMsgSubCommand.PLAYER_MENU_LIST);

        } catch (Exception ex) {
            LogServer.LogException("showListMenuPlayer: " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }
}
