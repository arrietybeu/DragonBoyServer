package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.model.player.Player;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.LogServer;
import nro.service.MonsterService;

@APacketHandler(54)
public class PlayerAttackMonsterHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            Player player = session.getPlayer();
            if (player == null) return;
            var mobId = message.reader().readByte();
            if (mobId != -1) {
                LogServer.LogWarning("mobId:" + mobId);
                MonsterService.getInstance().sendMonsterDie(player, mobId, player.getPlayerStats().getBaseDamage());
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
