package nro.server.controller.handler;

import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.service.model.entity.monster.Monster;
import nro.server.service.model.entity.player.Player;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.system.LogServer;

@APacketHandler(54)
public class PlayerAttackMonsterHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        if (player.getSkills() == null) return;
        if (player.getSkills().getSkillSelect() == null) return;
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
