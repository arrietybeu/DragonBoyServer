package nro.server.controller.handler;

import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;

@APacketHandler(34)
public class SkillSelectHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        try {
            int skillId = message.reader().readShort();
            player.getSkills().selectSkill(skillId);
        } catch (Exception ex) {
            LogServer.LogException("SkillSelectHandler: " + ex.getMessage(), ex);
        }
    }

}
