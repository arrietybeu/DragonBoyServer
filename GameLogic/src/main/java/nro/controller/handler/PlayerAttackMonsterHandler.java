package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.model.monster.Monster;
import nro.model.player.Player;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.LogServer;
import nro.service.MonsterService;
import nro.service.UseSkillService;

@APacketHandler(54)
public class PlayerAttackMonsterHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            Player player = session.getPlayer();
            if (player == null) return;
            var mobId = message.reader().readByte();

            Monster monster = player.getArea().getMonsterInAreaById(mobId);

            if (mobId != -1) {
                UseSkillService.getInstance().useSkillToAttackMonster(player, monster);
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
