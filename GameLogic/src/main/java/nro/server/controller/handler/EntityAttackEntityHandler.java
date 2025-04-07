package nro.server.controller.handler;

import nro.consts.ConstsCmd;
import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.system.LogServer;

@APacketHandler(ConstsCmd.PLAYER_ATTACK_PLAYER)
public class EntityAttackEntityHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        var player = session.getPlayer();
        if (player == null) return;
        try {
            var playerID = message.reader().readInt();
            var playerTarget = player.getArea().getAllEntity(playerID);
            if (playerTarget == null) return;
            System.out.println("playerTarget: " + playerTarget.getId());
            player.getSkills().useSkill(playerTarget);
        } catch (Exception e) {
            LogServer.LogException("AttackingEventHandler.process: " + e.getMessage(), e);
        }
    }
}
