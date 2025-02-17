package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.model.map.areas.Area;
import nro.model.player.Player;
import nro.network.Message;
import nro.network.Session;
import nro.server.LogServer;
import nro.service.AreaService;

@APacketHandler(-7)
public class PlayerMoveHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try{
            Player player = session.getPlayer();
            if (player == null) {
                return;
            }
            byte isOnGround = message.reader().readByte();

            short newX = message.reader().readShort();
            short newY = player.getY();

            if (message.available() > 0) {
                newY = message.reader().readShort();
            }

            if (Math.abs(newX - player.getX()) > 50 || Math.abs(newY - player.getY()) > 100) {
//                LogServer.LogWarning("Player " + player.getName() + " có di chuyển bất thường!");
//                return;
            }

            player.setX(newX);
            player.setY(newY);

            AreaService.getInstance().playerMove(player);
        }catch (Exception e){
            e.printStackTrace();
            LogServer.LogException("Error PlayerMoveHandler: " + e.getMessage());
        }
    }

}
