package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.model.player.Player;
import nro.server.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;

@APacketHandler(-45)
public class SkillNotFocusHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null)
            return;
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
                // useSkill.useSkillNotForcus(player, status);
            }
        } catch (Exception ex) {
            LogServer.LogException("SkillNotFocusHandler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
