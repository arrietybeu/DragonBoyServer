package nro.service;

import lombok.Getter;
import nro.model.map.GameMap;
import nro.model.map.Waypoint;
import nro.model.map.areas.Area;
import nro.model.player.Player;
import nro.network.Message;
import nro.server.LogServer;
import nro.server.manager.MapManager;

public class AreaService {

    @Getter
    public static final AreaService instance = new AreaService();

    public void playerMove(Player player) {
    }

    public void playerChangerMap(Player player) {
        var ms = System.currentTimeMillis();
        try {
            Area currentArea = player.getArea();
            GameMap currentMap = currentArea.getMap();
            Waypoint waypoint = currentMap.getWayPointInMap(player);
            Service service = Service.getInstance();

            if (waypoint == null) {
                this.keepPlayerInSafeZone(player);
                service.sendChatGlobal(player.getSession(), null, "Không tìm thấy Waypoint", false);
                return;
            }

            GameMap newMap = MapManager.getInstance().findMapById(waypoint.getGoMap());
            if (newMap == null) {
                this.keepPlayerInSafeZone(player);
                service.sendChatGlobal(player.getSession(), null, "Map không tồn tại", false);
                return;
            }

            this.gotoMap(player, newMap, waypoint.getGoX(), waypoint.getGoY());

        } catch (Exception ex) {
            LogServer.LogException("playerChangerMap: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void gotoMap(Player player, GameMap goMap, short goX, short goY) {
        Area currentArea = player.getArea();
        Area newArea = goMap.getArea();
        Service service = Service.getInstance();

        if (newArea == null) {
            this.keepPlayerInSafeZone(player);
            service.sendChatGlobal(player.getSession(), null, "Không có Area để vào", false);
            return;
        }

        currentArea.removePlayer(player);
        newArea.addPlayer(player);
        player.setArea(newArea);

        player.setX(goX);
        player.setY(goY);

        this.sendMessageChangerMap(player);
    }

    private void keepPlayerInSafeZone(Player player) {
        try {
            GameMap map = player.getArea().getMap();
            int safeX = player.getX();

            if (safeX >= map.getTileMap().tmh() - 60) {
                safeX = map.getTileMap().tmw() - 60;
            } else if (safeX <= 60) {
                safeX = 60;
            }
            player.setX((short) safeX);
            this.sendResetPoint(player);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // khi player qua map ma khong the qua duoc thi send messsage nay
    private void sendResetPoint(Player player) {
        try (Message message = new Message(46)) {
            message.writer().writeShort(player.getX());
            message.writer().writeShort(player.getY());
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("sendResetPoint: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void sendMessageChangerMap(Player player) {
        try {
            var playerService = PlayerService.getInstance();
            MapService.clearMap(player);
            playerService.sendStamina(player);
            playerService.sendCurrencyHpMp(player);
            MapService.getInstance().sendMapInfo(player);// -24
        } catch (Exception ex) {
            LogServer.LogException("Error send Message Changer Map: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
