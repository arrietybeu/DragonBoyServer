package nro.server.controller.handler;

import nro.consts.ConstsCmd;
import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.system.LogServer;

@APacketHandler(ConstsCmd.ITEM_SALE)
public class ItemSaleHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try{
            byte action = message.reader().readByte();
            byte type = message.reader().readByte();
            short id = message.reader().readShort();
        }catch (Exception exception){
            LogServer.LogException("ItemSaleHandler: " + exception.getMessage(), exception);
        }
    }

}
