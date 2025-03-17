package nro.service;

import lombok.Getter;
import nro.consts.ConstShop;
import nro.consts.ConstTypeObject;
import nro.consts.ConstsCmd;
import nro.model.item.Item;
import nro.model.item.ItemMap;
import nro.model.item.ItemTemplate;
import nro.model.map.GameMap;
import nro.model.map.areas.Area;
import nro.model.monster.Monster;
import nro.model.player.Player;
import nro.server.manager.*;
import nro.server.network.Message;
import nro.server.LogServer;
import nro.service.core.ItemFactory;
import nro.utils.FileNio;
import nro.utils.Util;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        } finally {
            this.commandForAdmins(player, text);
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

    private void commandForAdmins(Player playerChat, String text) {
        Service service = Service.getInstance();
        try {
            if (text.startsWith("find ")) {
                String keyword = text.substring(5).trim().toLowerCase();
                if (keyword.isEmpty()) {
                    service.sendChatGlobal(playerChat.getSession(), null, "Vui lòng nhập từ khóa để tìm!", false);
                    return;
                }

                List<Item> resultList = new ArrayList<>();
                for (var entry : ItemManager.getInstance().getItemTemplates().values()) {
                    Item item = ItemFactory.getInstance().createItemOptionsBase(entry.id());
                    String itemName = item.getTemplate().name();

                    String itemNameNoAccent = Util.removeDiacritics(itemName).toLowerCase();
                    String keywordNoAccent = Util.removeDiacritics(keyword).toLowerCase();

                    if (itemName.toLowerCase().contains(keyword) || itemNameNoAccent.contains(keywordNoAccent)) {
                        resultList.add(item);
                    }
                }

                if (resultList.isEmpty()) {
                    service.sendChatGlobal(playerChat.getSession(), null, "Không tìm thấy vật phẩm nào chứa: "
                            + keyword, false);
                } else {
                    String[] tableHeader = new String[]{"Size: " + resultList.size(), "", "", "", ""};
                    ShopService.getInstance().showShop(playerChat, keyword, ConstShop.SHOP_KY_GUI, resultList, tableHeader);
                }
                return;
            }
            if (text.startsWith("m ")) {
                int mapId = this.getNumber(text);
                AreaService.getInstance().changerMapByShip(playerChat, mapId, 1);
                service.sendChatGlobal(playerChat.getSession(), null, "Đã dịch chuyển đến map " + mapId, false);
                return;
            }
            if (text.startsWith("hp ")) {
                int hp = this.getNumber(text);
                playerChat.getPlayerPoints().setCurrentHp(hp);
                playerChat.getPlayerPoints().setCurrentMp(hp);
                PlayerService.getInstance().sendCurrencyHpMp(playerChat);
                service.sendChatGlobal(playerChat.getSession(), null, "Set HP: " + hp, false);
                return;
            }
            if (text.startsWith("it ")) {
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
            }
            if (text.startsWith("rm ")) {
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
            }
            if (text.startsWith("im ")) {
                int itemIdMap = this.getNumber(text);
                if (itemIdMap == -1) {
                    service.sendChatGlobal(playerChat.getSession(), null, "Item không hợp lệ: " + text, false);
                    return;
                }
                Item item = ItemFactory.getInstance().createItemOptionsBase(itemIdMap);
                ItemMap itemMap = new ItemMap(playerChat.getArea(), playerChat.getArea().increaseItemMapID(), playerChat.getId(), item, playerChat.getX(),
                        playerChat.getY(), -1, true);
                ItemService.getInstance().sendDropItemMap(playerChat, itemMap, true);
                service.sendChatGlobal(playerChat.getSession(), null, "Đã thêm item: " + itemIdMap, false);
                return;
            }
            if (text.startsWith("sd ")) {
                int damage = this.getNumber(text);
                if (damage == -1) {
                    service.sendChatGlobal(playerChat.getSession(), null, "Damage không hợp lệ: " + text, false);
                    return;
                }
                playerChat.getPlayerPoints().setBaseDamage(damage);
                service.sendChatGlobal(playerChat.getSession(), null, "Set Damage: " + damage, false);
                return;
            }
            if (text.startsWith("c ")) {
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
                case "spam_drop" -> {
                    int itemIdMap;
                    int x = playerChat.getX();
                    for (itemIdMap = 0; itemIdMap < 100; itemIdMap++) {
                        Item item = ItemFactory.getInstance().createItemOptionsBase(itemIdMap);
                        ItemMap itemMap = new ItemMap(playerChat.getArea(), playerChat.getArea().increaseItemMapID(), playerChat.getId(), item, x + 10,
                                playerChat.getY(), -1, true);
                        ItemService.getInstance().sendDropItemMap(playerChat, itemMap, true);
                    }
                    service.sendChatGlobal(playerChat.getSession(), null, "Đã thêm item: " + itemIdMap, false);
                }
                case "switch_update_map" -> {
                    MapManager.running = !MapManager.running;
                    service.sendChatGlobal(playerChat.getSession(), null, String.format(" update map %s Sussecs!", MapManager.running), false);
                }
                case "clear_cache" -> {
                    FileNio.clearCache();
                    service.sendChatGlobal(playerChat.getSession(), null, "Clear Cache Sussecs!", false);
                }
                case "bag" -> {
                    playerChat.getPlayerInventory().getItemsBag().add(ItemFactory.getInstance().createItemNull());
                    InventoryService.getInstance().sendItemToBags(playerChat, 0);
                    service.sendChatGlobal(playerChat.getSession(), null, "Đã thêm item vào bag", false);
                }
                case "log_exception" -> {
                    try {
                        int[] arr = new int[1];
                        System.out.println(arr[2]);
                    } catch (Exception ex) {
                        LogServer.LogException("Lỗi khi xử lý lệnh admin: " + ex.getMessage(), ex);
                    }
                }
                case "buff_item" -> {
                    // var idMax = ItemManager.getInstance().getItemTemplates().size();
                    // for (int itemId = idMax - 10; itemId < idMax; itemId++) {
                    System.out.println("slot: " + playerChat.getPlayerInventory().getCountEmptyBag());
                    for (int itemId = 0; itemId < playerChat.getPlayerInventory().getCountEmptyBag(); itemId++) {
                        Item item = ItemFactory.getInstance().createItemOptionsBase(itemId);
                        if (playerChat.getPlayerInventory().addItemBag(item)) {
                            service.sendChatGlobal(playerChat.getSession(), null, "Đã thêm item: " + itemId, false);
                        }
                    }
                }

                case "npc_size" -> {
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
                }
                case "cache" -> Service.dialogMessage(playerChat.getSession(), "Cache size: " + FileNio.CACHE.size());
                case "info" -> {
                    String threadInfo = "Thread const: " + Thread.activeCount() + " session size: "
                            + SessionManager.getInstance().getSizeSession();
                    String playerLocation = "\nPlayer Location mapId: "
                            + playerChat.getArea().getMap().getId() + " zone id: " + playerChat.getArea().getId()
                            + " x: " + playerChat.getX() + " y: " + playerChat.getY();
                    String content = threadInfo + playerLocation;
                    NpcService.getInstance().sendNpcTalkUI(playerChat, 5, content, -1);
                }
                case "reload_map" -> {
                    ManagerRegistry.reloadManager(MapManager.class);
                    service.sendChatGlobal(playerChat.getSession(), null, "Load Map Manager Thành Công", false);
                }
                case "reload_item" -> {
                    ManagerRegistry.reloadManager(ItemManager.class);
                    service.sendChatGlobal(playerChat.getSession(), null, "Load Item Manager Thành Công: "
                            + ItemManager.getInstance().getItemOptionTemplates().size(), false);
                }
                case "reload_task" -> {
                    ManagerRegistry.reloadManager(TaskManager.class);
                    service.sendChatGlobal(playerChat.getSession(), null, "Load Task Manager Thành Công", false);
                }
                case "area_check" -> {
                    var playerMapSize = playerChat.getArea().getPlayersByType(ConstTypeObject.TYPE_PLAYER).size();
                    var itemMapSize = playerChat.getArea().getItemsMap().size();
                    var monsterSize = playerChat.getArea().getMonsters().size();
                    var npcSize = playerChat.getArea().getNpcList().size();
                    var infoArea = "Player Size: " + playerMapSize + "\nitemMapSize: " + itemMapSize + "\nmonsterSize: "
                            + monsterSize + "\nnpcSize: " + npcSize;
                    Service.dialogMessage(playerChat.getSession(), infoArea);
                }
                case "remove_bag" -> {
                    playerChat.getPlayerInventory().removeAllItemBag();
                    service.sendChatGlobal(playerChat.getSession(), null, "Remove Bag Thành Công", false);
                }
                default ->
                        service.sendChatGlobal(playerChat.getSession(), null, "Command không hợp lệ: " + text, false);
            }
        } catch (NumberFormatException e) {
            service.sendChatGlobal(playerChat.getSession(), null, "Command không hợp lệ: " + text, false);
        } catch (Exception ex) {
            LogServer.LogException("Lỗi khi xử lý lệnh admin: " + ex.getMessage(), ex);
        }
    }

}
