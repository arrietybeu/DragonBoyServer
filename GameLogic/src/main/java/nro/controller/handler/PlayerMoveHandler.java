package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.service.model.entity.player.Player;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.system.LogServer;
import nro.service.core.map.AreaService;

@APacketHandler(-7)
public class PlayerMoveHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            Player player = session.getPlayer();
            if (player == null) return;

            if (player.getPlayerStatus().isLockMove()) return;

            byte isOnGround = message.reader().readByte();//  0: on ground, 1: in air

            if (isOnGround == 1) {
                player.getPoints().reduceMPWhenFlying();
            }

            short newX = message.reader().readShort();
            short newY = player.getY();

            if (message.available() > 0) {
                newY = message.reader().readShort();
            }

//            if (Util.getDistance(player.getX(), player.getY(), newX, newY) > 80) {
//                LogServer.LogWarning("Player " + player.getName() + " có di chuyển bất thường !\nX new: " + newX + " Y new: " + newY + "\nPlayer X: " + player.getX() + " Player Y: " + player.getY());
//            }

            player.setX(newX);
            player.setY(newY);

            if (player.getPlayerTask().getTaskMain().getId() == 0) {
                player.getPlayerTask().checkDoneTaskGoMap();
            }

            AreaService.getInstance().playerMove(player);
        } catch (Exception e) {
            LogServer.LogException("Error PlayerMoveHandler: " + e.getMessage(), e);
        }
    }

}
