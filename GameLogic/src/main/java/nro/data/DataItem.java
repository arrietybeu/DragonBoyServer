package nro.data;

import lombok.Getter;
import nro.network.Message;
import nro.network.Session;
import nro.server.config.ConfigServer;
import nro.consts.ConstMsgNotMap;
import nro.server.manager.item.ItemManager;
import nro.server.manager.item.ItemOptionManager;
import nro.model.item.ItemOptionTemplate;
import nro.model.item.ItemTemplate;
import nro.server.LogServer;

public class DataItem {

    @Getter
    private static final DataItem instance = new DataItem();

    private static final byte ITEM_OPTION = 0;
    private static final byte ITEM_NORMAL = 1;
    private static final byte ITEM_ARR_HEAD_2FR = 100;
    private static final byte ITEM_ARR_HEAD_FLYMOVE = 101;

    public void sendDataItem(Session session) {
        this.sendItemOptions(session);
        this.sendItemTemplate(session);
        this.sendItemArr_Head_2Fr(session);
        this.sendItemArr_Head_FlyMove(session);
        session.getSessionInfo().setUpdateItem(true);
    }

    private void sendItemOptions(Session session) {
        ItemOptionManager itemOptionManager = ItemOptionManager.getInstance();
        try (Message message = new Message(-28)) {
            message.writer().writeByte(ConstMsgNotMap.UPDATE_ITEM);//  8
            message.writer().writeByte(ConfigServer.VERSION_ITEM);// vcItem true
            message.writer().writeByte(ITEM_OPTION);// 0
            message.writer().writeByte(0); //update option
            message.writer().writeShort(itemOptionManager.getItemOptionTemplates().size());// dis true
            for (ItemOptionTemplate itemOptionTemplate : itemOptionManager.getItemOptionTemplates()) {
                message.writer().writeUTF(itemOptionTemplate.name());
                message.writer().writeByte(itemOptionTemplate.type());
            }
            session.doSendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sending skill template: " + e.getMessage());
        }
    }

    private void sendItemTemplate(Session session) {
        ItemManager itemManager = ItemManager.getInstance();
        try (Message message = new Message(12)) {
            message.writer().writeByte(0);
            message.writer().writeByte(ConfigServer.VERSION_ITEM);
            message.writer().writeByte(ITEM_NORMAL);
            message.writer().writeShort(itemManager.getItemTemplates().size());
            for (ItemTemplate itemTemplate : itemManager.getItemTemplates()) {
                message.writer().writeByte(itemTemplate.getType());
                message.writer().writeByte(itemTemplate.getGender());
                message.writer().writeUTF(itemTemplate.getName());
                message.writer().writeUTF(itemTemplate.getDescription());
                message.writer().writeByte(itemTemplate.getLevel());
                message.writer().writeInt(itemTemplate.getStrRequire());
                message.writer().writeShort(itemTemplate.getIconID());
                message.writer().writeShort(itemTemplate.getPart());
                message.writer().writeBoolean(itemTemplate.isUpToUp());
            }
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
            message.writer().writeByte(ITEM_ARR_HEAD_2FR);
            message.writer().writeShort(itemManager.getArrHead2Frames().size());
            for (ItemTemplate.ArrHead2Frames arrHead2Frames : itemManager.getArrHead2Frames()) {
                message.writer().writeByte(arrHead2Frames.getFrames().size());
                for (Integer head : arrHead2Frames.getFrames()) {
                    message.writer().writeShort(head);
                }
            }
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
            message.writer().writeByte(ITEM_ARR_HEAD_FLYMOVE);
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
