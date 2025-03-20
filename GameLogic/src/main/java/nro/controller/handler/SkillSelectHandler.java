package nro.controller.handler;

import nro.service.model.model.player.Player;
import nro.server.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;

@APacketHandler(34)
public class SkillSelectHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        try {
            int skillId = message.reader().readShort();
            player.getPlayerSkill().selectSkill(skillId);
        } catch (Exception ex) {
            LogServer.LogException("SkillSelectHandler: " + ex.getMessage(), ex);
        }
    }

}
