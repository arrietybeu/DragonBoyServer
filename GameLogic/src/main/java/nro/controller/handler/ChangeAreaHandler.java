package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.service.model.map.areas.Area;
import nro.service.model.entity.player.Player;
import nro.server.system.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.service.core.map.AreaService;
import nro.service.core.system.ServerService;

import java.util.List;


@APacketHandler(21)
public class ChangeAreaHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        if (player.getPoints().isDead()) return;
        try {
            var areaId = message.reader().readByte();
            List<Area> areaList = player.getArea().getMap().getAreas();
            if (areaId <= 0 && areaId >= areaList.size()) {
                ServerService.getInstance().sendChatGlobal(player.getSession(), null,
                        "Đã xảy ra lỗi\nvui lòng thao tác lại", false);
                return;
            }
            Area area = areaList.get(areaId);
            AreaService.getInstance().changeArea(player, area);
        } catch (Exception ex) {
            LogServer.LogException("ChangeAreaHandler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
