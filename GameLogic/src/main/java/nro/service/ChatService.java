package nro.service;

import lombok.Getter;
import nro.consts.ConstTypeObject;
import nro.consts.ConstsCmd;
import nro.model.item.Item;
import nro.model.item.ItemMap;
import nro.model.map.GameMap;
import nro.model.map.areas.Area;
import nro.model.monster.Monster;
import nro.model.player.Player;
import nro.server.manager.*;
import nro.server.network.Message;
import nro.server.LogServer;
import nro.service.core.ItemFactory;
import nro.utils.FileNio;

@SuppressWarnings("ALL")
public class ChatService {

    @Getter
    private static final ChatService instance = new ChatService();

    public void chatMap(Player player, String text) {
        try (Message message = new Message(ConstsCmd.CHAT_MAP)) {
            message.writer().writeInt(player.getId());
            message.writer().writeUTF(text);
            player.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("Error Service Chat Map: " + ex.getMessage(), ex);
        }
    }

    private int getNumber(String text) {
        try {
            return Integer.parseInt(text.substring(2).trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String[] getArrayString(String text) {
        return text.split(" ");
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
                    service.sendChatGlobal(playerChat.getSession(), null, "Không có khu vực trống trong map: " + mapId,
                            false);
                    return;
                }

                short x = 500;
                short y = 5;

                playerChat.getPlayerStatus().setTeleport(1);
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
                var parse = this.getArrayString(text);
                if (parse.length >= 3) {
                    var itemId = Integer.parseInt(parse[1]);
                    var quantity = Integer.parseInt(parse[2]);
                    if (itemId == -1) {
                        service.sendChatGlobal(playerChat.getSession(), null, "Item không hợp lệ: " + text, false);
                        return;
                    }
                    Item item = ItemFactory.getInstance().createItemOptionsBase(itemId, quantity);
                    playerChat.getPlayerInventory().addItemBag(item);
                    service.sendChatGlobal(playerChat.getSession(), null, "Đã thêm item: " + itemId, false);
                } else {
                    service.sendChatGlobal(playerChat.getSession(), null, "Lệnh không hợp lệ: " + text, false);
                }
                return;
            } else if (text.startsWith("rm ")) {
                int mobId = this.getNumber(text);
                if (mobId == -1) {
                    service.sendChatGlobal(playerChat.getSession(), null,
                            "Mob không hợp lệ: " + text, false);
                    return;
                }
                Monster monster = playerChat.getArea().getMonsterInAreaById(mobId);
                if (monster == null) {
                    service.sendChatGlobal(playerChat.getSession(), null, "Không tìm thấy mob: " + mobId, false);
                    return;
                }
                monster.setDie(playerChat, 500);
                service.sendChatGlobal(playerChat.getSession(), null, "Đã xóa mob: " + mobId, false);
                return;
            } else if (text.startsWith("im ")) {
                int itemIdMap = this.getNumber(text);
                if (itemIdMap == -1) {
                    service.sendChatGlobal(playerChat.getSession(), null, "Item không hợp lệ: " + text, false);
                    return;
                }
                Item item = ItemFactory.getInstance().createItemOptionsBase(itemIdMap);
                ItemMap itemMap = new ItemMap(playerChat.getArea(), playerChat.getId(), item, playerChat.getX(),
                        playerChat.getY(), -1);
                ItemService.getInstance().sendDropItemMap(playerChat, itemMap);
                service.sendChatGlobal(playerChat.getSession(), null, "Đã thêm item: " + itemIdMap, false);
                return;
            } else if (text.startsWith("sd ")) {
                int damage = this.getNumber(text);
                if (damage == -1) {
                    service.sendChatGlobal(playerChat.getSession(), null, "Damage không hợp lệ: " + text, false);
                    return;
                }
                playerChat.getPlayerPoints().setBaseDamage(damage);
                service.sendChatGlobal(playerChat.getSession(), null, "Set Damage: " + damage, false);
                return;
            } else if (text.startsWith("c ")) {
                int type = this.getNumber(text);
                if (type == -1) {
                    service.sendChatGlobal(playerChat.getSession(), null, "Type không hợp lệ: " + text, false);
                    return;
                }
                service.sendPetFollow(playerChat, type, -1);
                service.sendChatGlobal(playerChat.getSession(), null, "Call Pet: " + type, false);
                return;
            }
            switch (text) {
                case "bag": {
                    playerChat.getPlayerInventory().getItemsBag().add(ItemFactory.getInstance().createItemNull());
                    InventoryService.getInstance().sendItemToBags(playerChat, 0);
                    service.sendChatGlobal(playerChat.getSession(), null, "Đã thêm item vào bag", false);
                    break;
                }
                case "log_exception":
                    try {
                        int[] arr = new int[1];
                        System.out.println(arr[2]);
                    } catch (Exception ex) {
                        LogServer.LogException("Lỗi khi xử lý lệnh admin: " + ex.getMessage(), ex);
                    }
                    break;
                case "buff_item":
                    // var idMax = ItemManager.getInstance().getItemTemplates().size();
                    // for (int itemId = idMax - 10; itemId < idMax; itemId++) {
                    System.out.println("slot: " + playerChat.getPlayerInventory().getCountEmptyBag());
                    for (int itemId = 0; itemId < playerChat.getPlayerInventory().getCountEmptyBag(); itemId++) {
                        Item item = ItemFactory.getInstance().createItemOptionsBase(itemId);
                        if (playerChat.getPlayerInventory().addItemBag(item)) {
                            service.sendChatGlobal(playerChat.getSession(), null, "Đã thêm item: " + itemId, false);
                        }
                    }
                    break;
                case "send_exp": {
                    PlayerService.getInstance().sendPlayerUpExp(playerChat, 2, 100);
                    break;
                }
                case "send_task": {
                    playerChat.getPlayerTask().sendTaskInfo();
                    service.sendChatGlobal(playerChat.getSession(), null, "Send Task Thành Công", false);
                    break;
                }
                case "npc_size":
                    int sizeNpcAllArea = 0;
                    for (GameMap map : MapManager.getInstance().getGameMaps().values()) {
                        for (Area area : map.getAreas()) {
                            System.out.println("map id: " + map.getId() + " area id: " + area.getId() + " npc size: "
                                    + area.getNpcList().size());
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
                    String threadInfo = "Thread const: " + Thread.activeCount() + " session size: "
                            + SessionManager.getInstance().getSizeSession();
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
                    service.sendChatGlobal(playerChat.getSession(), null, "Load Item Manager Thành Công: "
                            + ItemManager.getInstance().getItemOptionTemplates().size(), false);
                    break;
                case "reload_task":
                    ManagerRegistry.reloadManager(TaskManager.class);
                    service.sendChatGlobal(playerChat.getSession(), null, "Load Task Manager Thành Công", false);
                    break;
                case "area_check":
                    var info = "Area Size PLayer: "
                            + playerChat.getArea().getPlayersByType(ConstTypeObject.TYPE_PLAYER).size();
                    service.sendChatGlobal(playerChat.getSession(), null, info, false);
                    break;
                default:
                    service.sendChatGlobal(playerChat.getSession(), null, "Command không hợp lệ: " + text, false);
                    break;
            }
        } catch (NumberFormatException e) {
            service.sendChatGlobal(playerChat.getSession(), null, "Command không hợp lệ: " + text, false);
        } catch (Exception ex) {
            LogServer.LogException("Lỗi khi xử lý lệnh admin: " + ex.getMessage(), ex);
        }
    }

}
