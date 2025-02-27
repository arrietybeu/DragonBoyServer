package nro.service;

import lombok.Getter;
import nro.consts.ConstTypeObject;
import nro.model.item.Item;
import nro.model.map.GameMap;
import nro.model.map.areas.Area;
import nro.model.monster.Monster;
import nro.model.player.Player;
import nro.server.manager.*;
import nro.server.network.Message;
import nro.server.LogServer;
import nro.utils.FileNio;

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

    private int getNumber(String text) {
        try {
            return Integer.parseInt(text.substring(2).trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void commandForAdmins(Player playerChat, String text) {
        Service service = Service.getInstance();
        try {
            if (text.startsWith("m ")) {
                int mapId = this.getNumber(text);
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
            } else if (text.startsWith("hp ")) {
                int hp = this.getNumber(text);
                playerChat.getPlayerPoints().setCurrentHp(hp);
                playerChat.getPlayerPoints().setCurrentMp(hp);
                PlayerService.getInstance().sendCurrencyHpMp(playerChat);
                service.sendChatGlobal(playerChat.getSession(), null, "Set HP: " + hp, false);
                return;
            } else if (text.startsWith("it ")) {
                int itemId = this.getNumber(text);
                if (itemId == -1) {
                    service.sendChatGlobal(playerChat.getSession(), null, "Item không hợp lệ: " + text, false);
                    return;
                }
                Item item = ItemService.createAndInitItem(itemId);
                playerChat.getPlayerInventory().addItemBag(item);
                service.sendChatGlobal(playerChat.getSession(), null, "Đã thêm item: " + itemId, false);
                return;
            } else if (text.startsWith("rm ")) {
                int mobId = this.getNumber(text);
                if (mobId == -1) {
                    service.sendChatGlobal(playerChat.getSession(), null, "Mob không hợp lệ: " + text, false);
                    return;
                }
                Monster monster = playerChat.getArea().getMonsterInAreaById(mobId);
                if (monster == null) {
                    service.sendChatGlobal(playerChat.getSession(), null, "Không tìm thấy mob: " + mobId, false);
                    return;
                }
                monster.die(playerChat, 500);
                service.sendChatGlobal(playerChat.getSession(), null, "Đã xóa mob: " + mobId, false);
                return;
            }
            switch (text) {
                case "send_task": {
                    playerChat.getPlayerTask().sendTaskInfo();
                    service.sendChatGlobal(playerChat.getSession(), null, "Send Task Thành Công", false);
                    break;
                }
                case "npc_size":
                    int sizeNpcAllArea = 0;

                    for (GameMap map : MapManager.getInstance().getGameMaps().values()) {
                        for (Area area : map.getAreas()) {
                            System.out.println("map id: " + map.getId() + " area id: " + area.getId() + " npc size: " + area.getNpcList().size());
                            sizeNpcAllArea += area.getNpcList().size();
                        }
                    }

                    String infoNpcSize = "Npc Size: " + sizeNpcAllArea;
                    Service.dialogMessage(playerChat.getSession(), infoNpcSize);
                    break;
                case "cache":
                    Service.dialogMessage(playerChat.getSession(), "Cache size: " + FileNio.CACHE.size());
                    break;
                case "info":
                    String threadInfo = "Thread const: " + Thread.activeCount() + " session size: " + SessionManager.getInstance().getSizeSession();
                    String playerLocation = "\nPlayer Location mapId: "
                            + playerChat.getArea().getMap().getId() + " zone id: " + playerChat.getArea().getId()
                            + " x: " + playerChat.getX() + " y: " + playerChat.getY();
                    String content = threadInfo + playerLocation;
                    NpcService.getInstance().sendNpcTalkUI(playerChat, 5, content, -1);
                    break;
                case "reload_map":
                    ManagerRegistry.reloadManager(MapManager.class);
                    service.sendChatGlobal(playerChat.getSession(), null, "Load Map Manager Thành Công", false);
                    break;
                case "reload_item":
                    ManagerRegistry.reloadManager(ItemManager.class);
                    service.sendChatGlobal(playerChat.getSession(), null, "Load Item Manager Thành Công: " + ItemManager.getInstance().getItemOptionTemplates().size(), false);
                    break;
                case "reload_task":
                    ManagerRegistry.reloadManager(TaskManager.class);
                    service.sendChatGlobal(playerChat.getSession(), null, "Load Task Manager Thành Công", false);
                    break;
                case "area_check":
                    var info = "Area Size PLayer: " + playerChat.getArea().getPlayersByType(ConstTypeObject.TYPE_PLAYER).size();
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
