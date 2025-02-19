package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.network.Message;
import nro.network.Session;
import nro.server.LogServer;

@APacketHandler(54)
public class PlayerAttackMonsterHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            var mobId = message.reader().readByte();
            if (mobId != -1) {
                LogServer.LogWarning("mobId:" + mobId);
            } else {
                var monsterID = message.reader().readInt();
                LogServer.LogWarning("monsterId:" + monsterID);
            }
        } catch (Exception ex) {
            LogServer.LogException("PlayerAttackMonsterHandler: " + ex.getMessage());
            ex.printStackTrace();
        }

    }
}
