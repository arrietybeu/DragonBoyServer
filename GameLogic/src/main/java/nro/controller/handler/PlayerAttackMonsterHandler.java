package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.service.model.entity.monster.Monster;
import nro.service.model.entity.player.Player;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.system.LogServer;

@APacketHandler(54)
public class PlayerAttackMonsterHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        try {
            var mobId = message.reader().readByte();
            Monster monster = player.getArea().getMonsterInAreaById(mobId);
            if (monster == null) return;
            if (mobId != -1) {
                player.getSkills().entityAttackMonster(monster);
            } else {
                var monsterID = message.reader().readInt();
                LogServer.LogWarning("monsterId:" + monsterID);
            }
        } catch (Exception ex) {
            LogServer.LogException("PlayerAttackMonsterHandler: " + ex.getMessage(), ex);
        }
    }
}
