package nro.service;

import lombok.Getter;
import nro.model.map.GameMap;
import nro.model.map.areas.Area;
import nro.model.player.Player;
import nro.network.Message;
import nro.server.LogServer;
import nro.server.manager.ManagerRegistry;
import nro.server.manager.MapManager;

public class ChatService {

    @Getter
    private static final ChatService instance = new ChatService();

    public void chatMap(Player player, String text) {
        try (Message message = new Message(44)) {
            message.writer().writeInt(player.getId());
            message.writer().writeUTF(text);
            player.getArea().sendMessageToPlayersInArea(message, null);

        } catch (Exception ex) {
            LogServer.LogException("Error Service Chat Map: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void commandForAdmins(Player playerChat, String text) {
        Service service = Service.getInstance();
        try {
            if (text.startsWith("m ")) {
                int mapId = Integer.parseInt(text.substring(2).trim());

                GameMap newMap = MapManager.getInstance().findMapById(mapId);
                if (newMap == null) {
                    service.sendChatGlobal(playerChat.getSession(), null, "Map không tồn tại: " + mapId, false);
                    return;
                }

                Area newArea = newMap.getArea();
                if (newArea == null) {
                    service.sendChatGlobal(playerChat.getSession(), null, "Không có khu vực trống trong map: " + mapId, false);
                    return;
                }

                short x = 500;
                short y = (short) newArea.getMap().yPhysicInTop(x, 0);

                AreaService.getInstance().gotoMap(playerChat, newMap, x, y);
                service.sendChatGlobal(playerChat.getSession(), null, "Đã dịch chuyển đến map " + mapId, false);
                return;
            }
            switch (text) {
                case "info":
                    service.sendChatGlobal(playerChat.getSession(), null, "Thread const: " + Thread.activeCount(), false);
                    break;
                case "reload_map":
                    ManagerRegistry.reloadManager(MapManager.class);
                    service.sendChatGlobal(playerChat.getSession(), null, "Load Map Manager Thành Công", false);
                    break;
                case "area_check":
                    var info = "Area Size PLayer: " + playerChat.getArea().getPlayers().size();
                    service.sendChatGlobal(playerChat.getSession(), null, info, false);
                    break;
                default:
                    service.sendChatGlobal(playerChat.getSession(), null, "Command không hợp lệ: " + text, false);
                    break;
            }
        } catch (NumberFormatException e) {
            service.sendChatGlobal(playerChat.getSession(), null, "Command không hợp lệ: " + text, false);
        } catch (Exception ex) {
            LogServer.LogException("Lỗi khi xử lý lệnh admin: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
