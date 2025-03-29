package nro.server.controller.handler;

import nro.server.service.model.entity.player.Player;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.system.LogServer;
import nro.server.service.core.map.AreaService;

@APacketHandler(-39)
public class FinishLoadMapHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            Player player = session.getPlayer();
            if (player == null) return;
            AreaService.getInstance().sendInfoAllLiveObjectsTo(player);
        } catch (Exception ex) {
            LogServer.LogException("FinishLoadMapHandler: " + ex.getMessage(), ex);
        }
    }
}
