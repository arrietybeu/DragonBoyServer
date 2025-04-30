package nro.server.network.nro.client_packets.handler;

import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.client_packets.AClientPacketHandler;

import java.util.Set;

@AClientPacketHandler(command = 21, validStates = {NroConnection.State.IN_GAME})
public class CMChangeArea extends NroClientPacket {

    private byte areaId;

    public CMChangeArea(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        areaId = readByte();
    }

    @Override
    protected void runImpl() {
        /**
         *         Player player = getConnection().getActivePlayer();
         *         if (player == null || player.getPoints().isDead()) return;
         *         try {
         *             var areaId = message.reader().readByte();
         *             List<Area> areaList = player.getArea().getMap().getAreas();
         *             if (areaId <= 0 && areaId >= areaList.size()) {
         *                 ServerService.getInstance().sendChatGlobal(session, null,
         *                         "Đã xảy ra lỗi\nvui lòng thao tác lại", false);
         *                 return;
         *             }
         *             Area area = areaList.get(areaId);
         *             if (area == null) {
         *                 ServerService.getInstance().sendChatGlobal(session, null,
         *                         "Đã xảy ra lỗi\nvui lòng thao tác lại", false);
         *                 LogServer.LogException("ChangeAreaHandler: area is null " + areaId);
         *                 return;
         *             }
         *             if (area.getMap().isMapOffline() && !session.getUserInfo().isAdmin()) {
         *                 ServerService.dialogMessage(session, "Không thể đổi khu vực trong map này");
         *                 return;
         *             }
         *             AreaService.getInstance().changeArea(player, area);
         *         } catch (Exception ex) {
         *             LogServer.LogException("ChangeAreaHandler: " + ex.getMessage(), ex);
         *         }
         */
    }

}
