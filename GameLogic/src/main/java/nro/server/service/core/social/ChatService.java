package nro.server.service.core.social;

import lombok.Getter;
import nro.consts.*;
import nro.server.manager.entity.BossManager;
import nro.server.realtime.system.boss.BossAISystem;
import nro.server.realtime.system.item.ItemMapSystem;
import nro.server.realtime.system.player.PlayerSystem;
import nro.server.service.core.dragon.DragonService;
import nro.server.service.core.npc.NpcService;
import nro.server.service.core.system.ServerService;
import nro.server.service.core.economy.ShopService;
import nro.server.service.core.map.AreaService;
import nro.server.service.core.player.InventoryService;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.ai.boss.Boss;
import nro.server.service.model.entity.ai.boss.BossFactory;
import nro.server.service.model.item.Item;
import nro.server.service.model.item.ItemMap;
import nro.server.service.model.map.GameMap;
import nro.server.service.model.map.areas.Area;
import nro.server.service.model.entity.monster.Monster;
import nro.server.service.model.entity.player.Player;
import nro.server.manager.*;
import nro.server.network.Message;
import nro.server.system.LogServer;
import nro.server.service.core.item.ItemFactory;
import nro.server.service.core.item.ItemService;
import nro.server.service.core.player.PlayerService;
import nro.utils.FileNio;
import nro.utils.Util;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class ChatService {

    @Getter
    private static final ChatService instance = new ChatService();

    public void chatMap(Entity entity, String text) {
        try (Message message = new Message(ConstsCmd.CHAT_MAP)) {
            message.writer().writeInt(entity.getId());
            message.writer().writeUTF(text);
            entity.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("Error ServerService Chat Map: " + ex.getMessage(), ex);
        } finally {
            if (entity instanceof Player player) {
                if (player.isAdministrator()) {
                    this.commandForAdmins(player, text);
                }
            }
        }
    }

    private void commandForAdmins(Player playerChat, String text) {
        ServerService serverService = ServerService.getInstance();
        try {
            if (text.startsWith("find ")) {
                String keyword = text.substring(5).trim().toLowerCase();
                if (keyword.isEmpty()) {
                    serverService.sendChatGlobal(playerChat.getSession(), null, "Vui lòng nhập từ khóa để tìm!", false);
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
                    serverService.sendChatGlobal(playerChat.getSession(), null, "Không tìm thấy vật phẩm nào chứa: " + keyword, false);
                } else {
                    String[] tableHeader = new String[]{"Size: " + resultList.size(), "", "", "", ""};
                    ShopService.getInstance().showShop(playerChat, keyword, ConstShop.SHOP_KY_GUI, resultList, tableHeader);
                }
                return;
            }
            if (text.startsWith("m ")) {
                int mapId = (int) this.getNumber(text);
                AreaService.getInstance().changerMapByShip(playerChat, mapId, Util.nextInt(400, 444), 5, 1);
                serverService.sendChatGlobal(playerChat.getSession(), null, "Đã dịch chuyển đến map " + mapId, false);
                return;
            }
            if (text.startsWith("hp ")) {
                long hp = this.getNumber(text);
                playerChat.getPoints().setCurrentHp(hp);
                playerChat.getPoints().setCurrentMp(hp);
                PlayerService.getInstance().sendCurrencyHpMp(playerChat);
                serverService.sendChatGlobal(playerChat.getSession(), null, "Set HP: " + hp, false);
                return;
            }
            if (text.startsWith("s ")) {
                int idShop = (int) this.getNumber(text);
                ShopService.getInstance().sendNornalShop(playerChat, idShop);
                serverService.sendChatGlobal(playerChat.getSession(), null, "OpenShop Id: " + idShop, false);
                return;
            }
            if (text.startsWith("tn ")) {
                long exp = this.getNumber(text);
                playerChat.getPoints().addExp(ConstPlayer.ADD_POWER_AND_EXP, exp);
                serverService.sendChatGlobal(playerChat.getSession(), null, "SET EXP: " + exp, false);
                return;
            }
            if (text.startsWith("gt ")) {
                long exp = this.getNumber(text);
                playerChat.getPoints().addExp(ConstPlayer.ADD_POWER_AND_EXP, exp);
                serverService.sendChatGlobal(playerChat.getSession(), null, "SET EXP: " + exp, false);
                return;
            }
            if (text.startsWith("it ")) {
                var parse = this.getArrayString(text);
                if (parse.length >= 3) {
                    var itemId = Integer.parseInt(parse[1]);
                    var quantity = Integer.parseInt(parse[2]);
                    if (itemId == -1) {
                        serverService.sendChatGlobal(playerChat.getSession(), null, "Item không hợp lệ: " + text, false);
                        return;
                    }
                    Item item = ItemFactory.getInstance().createItemOptionsBase(itemId, quantity);
                    playerChat.getPlayerInventory().addItemBag(item);
                    serverService.sendChatGlobal(playerChat.getSession(), null, "Đã thêm item: " + itemId, false);
                } else {
                    serverService.sendChatGlobal(playerChat.getSession(), null, "Lệnh không hợp lệ: " + text, false);
                }
                return;
            }
            if (text.startsWith("rm ")) {
                int mobId = (int) this.getNumber(text);
                if (mobId == -1) {
                    serverService.sendChatGlobal(playerChat.getSession(), null, "Mob không hợp lệ: " + text, false);
                    return;
                }
                Monster monster = playerChat.getArea().getMonsterInAreaById(mobId);
                if (monster == null) {
                    serverService.sendChatGlobal(playerChat.getSession(), null, "Không tìm thấy mob: " + mobId, false);
                    return;
                }
                monster.setDie(playerChat, 500);
                serverService.sendChatGlobal(playerChat.getSession(), null, "Đã xóa mob: " + mobId, false);
                return;
            }
            if (text.startsWith("im ")) {
                int itemIdMap = (int) this.getNumber(text);
                if (itemIdMap == -1) {
                    serverService.sendChatGlobal(playerChat.getSession(), null, "Item không hợp lệ: " + text, false);
                    return;
                }
                Item item = ItemFactory.getInstance().createItemOptionsBase(itemIdMap);
                ItemMap itemMap = new ItemMap(playerChat.getArea(), playerChat.getArea().increaseItemMapID(), playerChat.getId(), item, playerChat.getX(), playerChat.getY(), -1, true);
                ItemService.getInstance().sendDropItemMap(playerChat, itemMap, true);
                serverService.sendChatGlobal(playerChat.getSession(), null, "Đã thêm item: " + itemIdMap, false);
                return;
            }
            if (text.startsWith("sd ")) {
                int damage = (int) this.getNumber(text);
                if (damage == -1) {
                    serverService.sendChatGlobal(playerChat.getSession(), null, "Damage không hợp lệ: " + text, false);
                    return;
                }
                playerChat.getPoints().setBaseDamage(damage);
                serverService.sendChatGlobal(playerChat.getSession(), null, "Set Damage: " + damage, false);
                return;
            }
            if (text.startsWith("c ")) {
                int type = (int) this.getNumber(text);
                if (type == -1) {
                    serverService.sendChatGlobal(playerChat.getSession(), null, "Type không hợp lệ: " + text, false);
                    return;
                }
                serverService.sendPetFollow(playerChat, type, -1);
                serverService.sendChatGlobal(playerChat.getSession(), null, "Call Pet: " + type, false);
                return;
            }

            if (text.startsWith("dr ")) {
                int type = (int) this.getNumber(text);

                if (type == -1) {
                    serverService.sendChatGlobal(playerChat.getSession(), null, "Type không hợp lệ: " + text, false);
                    return;
                }
                // 0 appear, 1 hide , 2 call dragon namec
                boolean isDragonNamec = type == 2;
                if (isDragonNamec) type = 0;
                DragonService.getInstance().sendShenronDragon(playerChat, type, isDragonNamec);
                serverService.sendChatGlobal(playerChat.getSession(), null, "Call Dragon: " + type, false);
                return;
            }
            switch (text) {
                case "tau77" -> {
                    BossFactory.getInstance().trySpawnSpecialBossInArea(playerChat, playerChat.getArea(), ConstBoss.TAU_PAY_PAY);
                    serverService.sendChatGlobal(playerChat.getSession(), null, "Spawn Boss: " + ConstBoss.TAU_PAY_PAY, false);
                }
                case "spam_drop" -> {
                    int itemIdMap;
                    int x = playerChat.getX();
                    for (itemIdMap = 0; itemIdMap < 100; itemIdMap++) {
                        Item item = ItemFactory.getInstance().createItemOptionsBase(itemIdMap);
                        ItemMap itemMap = new ItemMap(playerChat.getArea(), playerChat.getArea().increaseItemMapID(), playerChat.getId(), item, x + 10, playerChat.getY(), -1, true);
                        ItemService.getInstance().sendDropItemMap(playerChat, itemMap, true);
                    }
                    serverService.sendChatGlobal(playerChat.getSession(), null, "Đã thêm item: " + itemIdMap, false);
                }
                case "clear_cache" -> {
                    FileNio.clearCache();
                    serverService.sendChatGlobal(playerChat.getSession(), null, "Clear Cache Sussecs!", false);
                }
                case "bag" -> {
                    playerChat.getPlayerInventory().getItemsBag().add(ItemFactory.getInstance().createItemNull());
                    InventoryService.getInstance().sendItemToBags(playerChat, 0);
                    serverService.sendChatGlobal(playerChat.getSession(), null, "Đã thêm item vào bag", false);
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
                            serverService.sendChatGlobal(playerChat.getSession(), null, "Đã thêm item: " + itemId, false);
                        }
                    }
                }

                case "npc_size" -> {
                    int sizeNpcAllArea = 0;
                    for (GameMap map : MapManager.getInstance().getGameMaps().values()) {
                        for (Area area : map.getAreas()) {
                            System.out.println("map id: " + map.getId() + " area id: " + area.getId() + " npc size: " + area.getNpcList().size());
                            sizeNpcAllArea += area.getNpcList().size();
                        }
                    }
                    String infoNpcSize = "Npc Size: " + sizeNpcAllArea;
                    ServerService.dialogMessage(playerChat.getSession(), infoNpcSize);
                }
                case "chat_vip" -> {
                    serverService.sendChatVip(playerChat, "mew mew");
                }
                case "cache" ->
                        ServerService.dialogMessage(playerChat.getSession(), "Cache size: " + FileNio.CACHE.size());
                case "info" -> {
                    String threadInfo = "Thread const: " + Thread.activeCount() + " session size: " + SessionManager.getInstance().getSizeSession();
                    String playerLocation = "\nPlayer Location mapId: " + playerChat.getArea().getMap().getId() + " zone id: " + playerChat.getArea().getId() + " x: " + playerChat.getX() + " y: " + playerChat.getY();

                    var sizePlayer = PlayerSystem.getInstance().size();
                    var sizeAreaItemMap = ItemMapSystem.getInstance().size();
                    String systemInfo = "\nsize player magic tree: " + sizePlayer + " size itemMap: " + sizeAreaItemMap;

                    String content = threadInfo + playerLocation + systemInfo;
                    NpcService.getInstance().sendNpcTalkUI(playerChat, 5, content, -1);
                }
                case "info_boss" -> {
                    String bossState = "";
                    for (var boss : BossAISystem.getInstance().getBosses().values()) {
                        bossState += "Boss: " + boss.getName() + " - State: " + boss.getState() + "\n";
                    }
                    String content = "Boss core size: " + BossAISystem.getInstance().size() + "\n" +
                            "Boss size: " + BossManager.getInstance().size() + "\n"
                            + bossState;
                    ServerService.dialogMessage(playerChat.getSession(), content);
                }
                case "reload_map" -> {
                    ManagerRegistry.reloadManager(MapManager.class);
                    serverService.sendChatGlobal(playerChat.getSession(), null, "Load Map Manager Thành Công", false);
                }
                case "reload_shop" -> {
                    ManagerRegistry.reloadManager(ShopManager.class);
                    serverService.sendChatGlobal(playerChat.getSession(), null, "Load Item Shop Manager Thành Công", false);
                }
                case "reload_item" -> {
                    ManagerRegistry.reloadManager(ItemManager.class);
                    serverService.sendChatGlobal(playerChat.getSession(), null, "Load Item Manager Thành Công: " + ItemManager.getInstance().getItemOptionTemplates().size(), false);
                }
                case "reload_task" -> {
                    ManagerRegistry.reloadManager(TaskManager.class);
                    serverService.sendChatGlobal(playerChat.getSession(), null, "Load Task Manager Thành Công", false);
                }
                case "area_check" -> {
                    var playerMapSize = playerChat.getArea().getPlayersByType(ConstTypeObject.TYPE_PLAYER).size();
                    var bossSize = playerChat.getArea().getPlayersByType(ConstTypeObject.TYPE_BOSS).size();
                    var playerSizeAllArea = MapManager.getInstance().checkAllPlayerInGame();
                    var itemMapSize = playerChat.getArea().getItemsMap().size();
                    var monsterSize = playerChat.getArea().getMonsters().size();
                    var npcSize = playerChat.getArea().getNpcList().size();
                    var infoArea = "Player Size: " + playerMapSize + "\nBoss size: " + bossSize + "\nSize player all area: " + playerSizeAllArea + "\nitemMapSize: " + itemMapSize + "\nmonsterSize: " + monsterSize + "\nnpcSize: " + npcSize;
                    ServerService.dialogMessage(playerChat.getSession(), infoArea);
                }

                case "remove_bag" -> {
                    playerChat.getPlayerInventory().removeAllItemBag();
                    serverService.sendChatGlobal(playerChat.getSession(), null, "Remove Bag Thành Công", false);
                }
                default ->
                        serverService.sendChatGlobal(playerChat.getSession(), null, "Command không hợp lệ: " + text, false);
            }
        } catch (NumberFormatException e) {
            serverService.sendChatGlobal(playerChat.getSession(), null, "Command không hợp lệ: " + text, false);
        } catch (Exception ex) {
            LogServer.LogException("Lỗi khi xử lý lệnh admin: " + ex.getMessage(), ex);
        }
    }

    private long getNumber(String text) {
        try {
            return Long.parseLong(text.substring(2).trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String[] getArrayString(String text) {
        return text.split(" ");
    }
}
