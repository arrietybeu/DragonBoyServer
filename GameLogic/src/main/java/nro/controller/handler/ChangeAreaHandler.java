package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.model.map.areas.Area;
import nro.model.player.Player;
import nro.server.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.service.AreaService;
import nro.service.Service;

import java.util.List;


@APacketHandler(21)
public class ChangeAreaHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        try {
            var areaId = message.reader().readByte();
            List<Area> areaList = player.getArea().getMap().getAreas();
            if (areaId <= 0 && areaId >= areaList.size()) {
                Service.getInstance().sendChatGlobal(player.getSession(), null,
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
