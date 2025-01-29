package nro.data;

import lombok.Getter;
import nro.network.Message;
import nro.network.Session;
import nro.server.config.ConfigServer;
import nro.consts.ConstMsgNotMap;
import nro.server.manager.ItemManager;
import nro.server.LogServer;

public class DataItem {

    @Getter
    private static final DataItem instance = new DataItem();

    public void sendDataItem(Session session) {
        this.sendItemOptions(session);
        this.sendItemTemplate(session);
        this.sendItemArr_Head_2Fr(session);
        this.sendItemArr_Head_FlyMove(session);
        session.getSessionInfo().setUpdateItem(true);
    }

    private void sendItemOptions(Session session) {
        ItemManager itemManager = ItemManager.getInstance();
        try (Message message = new Message(-28)) {
            message.writer().writeByte(ConstMsgNotMap.UPDATE_ITEM);//  8
            message.writer().writeByte(ConfigServer.VERSION_ITEM);// vcItem true
            message.writer().write(itemManager.getDataItemOption());// 0
            session.doSendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sending skill template: " + e.getMessage());
        }
    }

    private void sendItemTemplate(Session session) {
        ItemManager itemManager = ItemManager.getInstance();
        try (Message message = new Message(12)) {
            message.writer().write(itemManager.getDataItemTemplate());
            session.doSendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sending item template: " + e.getMessage());
        }
    }

    private void sendItemArr_Head_2Fr(Session session) {
        ItemManager itemManager = ItemManager.getInstance();
        try (Message message = new Message(-28)) {
            message.writer().writeByte(ConstMsgNotMap.UPDATE_ITEM);
            message.writer().writeByte(ConfigServer.VERSION_ITEM);
            message.writer().write(itemManager.getDataArrHead2Fr());
            session.doSendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sending item arr head 2 fr: " + e.getMessage());
        }
    }

    public void sendItemArr_Head_FlyMove(Session session) {
        // TODO 13/12/2024 Server send
        try (Message message = new Message(-28)) {
            message.writer().writeByte(ConstMsgNotMap.UPDATE_ITEM);
            message.writer().writeByte(ConfigServer.VERSION_ITEM);
            message.writer().writeByte(101);
            message.writer().writeShort(2);
            for (int i = 0; i < 2; i++) {
                message.writer().writeShort(1398);
                message.writer().writeShort(1401);
            }
            session.doSendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sending item arr head fly move: " + e.getMessage());
        }
    }

}
