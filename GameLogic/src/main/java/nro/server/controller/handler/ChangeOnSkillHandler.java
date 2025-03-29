package nro.server.controller.handler;

import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;

@APacketHandler(-113)
public class ChangeOnSkillHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        if (player.getPoints().isDead()) return;
        try {
            for (int i = 0; i < player.getSkills().getSkillShortCut().length; i++) {
                int skillId = message.reader().readByte();
                player.getSkills().getSkillShortCut()[i] = (byte) skillId;
            }
        } catch (Exception ex) {
            LogServer.LogException("ChangeOnSkillHandler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
