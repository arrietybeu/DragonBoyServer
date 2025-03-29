package nro.server.controller.handler;

import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;

@APacketHandler(-45)
public class SkillNotFocusHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        if (player.getSkills() == null) return;
        if (player.getSkills().getSkillSelect() == null) return;
        try {
            byte status = message.reader().readByte();
            if (status == 20) {
                byte skillId = message.reader().readByte();
                short cx = message.reader().readShort();
                short cy = message.reader().readShort();
                byte dir = message.reader().readByte();
                short x = message.reader().readShort();
                short y = message.reader().readShort();
                // useSkill.useSkillNotForcusNew(player, skillId, cx, cy, dir, x, y);
            } else {
                player.getSkills().useSkill(player);
            }
        } catch (Exception ex) {
            LogServer.LogException("SkillNotFocusHandler: " + ex.getMessage(), ex);
        }
    }

}
