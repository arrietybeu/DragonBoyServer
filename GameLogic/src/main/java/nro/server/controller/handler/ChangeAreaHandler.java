package nro.server.controller.handler;

import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.service.model.map.areas.Area;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.service.core.map.AreaService;
import nro.server.service.core.system.ServerService;

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
            if (area == null) {
                ServerService.getInstance().sendChatGlobal(player.getSession(), null,
                        "Đã xảy ra lỗi\nvui lòng thao tác lại", false);
                LogServer.LogException("ChangeAreaHandler: area is null " + areaId);
                return;
            }
            AreaService.getInstance().changeArea(player, area);
        } catch (Exception ex) {
            LogServer.LogException("ChangeAreaHandler: " + ex.getMessage(), ex);
        }
    }
}
