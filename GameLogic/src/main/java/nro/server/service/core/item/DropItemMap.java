package nro.server.service.core.item;

import nro.consts.ConstItem;
import nro.consts.ConstMap;
import nro.consts.ConstMonster;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.item.Item;
import nro.server.service.model.item.ItemMap;
import nro.server.service.model.entity.monster.Monster;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.entity.player.PlayerTask;
import nro.utils.Rnd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DropItemMap {

    private static final class SingletonHolder {
        private static final DropItemMap instance = new DropItemMap();
    }

    public static DropItemMap getInstance() {
        return SingletonHolder.instance;
    }

    private static final Map<Short, List<ItemDropInfo>> MAP_ITEM_DROPS = Map.of(
            ConstMap.NHA_GOHAN, List.of(new ItemDropInfo(ConstItem.DUI_GA_NUONG, 633, 315, 3, 0, false)),
            ConstMap.NHA_MOORI, List.of(new ItemDropInfo(ConstItem.DUI_GA_NUONG, 56, 315, 3, 0, false)),
            ConstMap.NHA_BROLY, List.of(new ItemDropInfo(ConstItem.DUI_GA_NUONG, 633, 320, 3, 0, false)),
            ConstMap.VACH_NUI_ARU, List.of(new ItemDropInfo(ConstItem.DUA_BE, 155, 288, 3, 1, true)),
            ConstMap.VACH_NUI_MOORI, List.of(new ItemDropInfo(ConstItem.DUA_BE, 136, 264, 3, 1, true)),
            ConstMap.VAC_NUI_KAKAROT, List.of(new ItemDropInfo(ConstItem.DUA_BE, 155, 288, 3, 1, true))
    );

    public List<ItemMap> dropItemMapForMonster(Entity entity, Monster monster) {
        switch (entity) {
            case Player player -> {
                List<ItemMap> itemMaps = new ArrayList<>();
                this.dropItemMapToTask(player, monster, itemMaps);
                return itemMaps;
            }
            default -> throw new IllegalStateException("dropItemMapForMonster: Unexpected value: " + entity);
        }
    }

    private void dropItemMapToTask(Player player, Monster monster, List<ItemMap> itemMaps) {
        PlayerTask playerTask = player.getPlayerTask();
        switch (monster.getTemplateId()) {
            case ConstMonster.KHUNG_LONG, ConstMonster.LON_LOI, ConstMonster.QUY_DAT -> {
                if (playerTask.getTaskMain().getId() == 2) {
                    if (playerTask.getTaskMain().getIndex() != 0) return;
                    Item item = ItemFactory.getInstance().createItemNotOptionsBase(ConstItem.DUI_GA, player.getId(), 1);
                    ItemMap itemMap = new ItemMap(monster.getArea(), monster.getArea().increaseItemMapID(), player.getId(), item, monster.getX(), monster.getY(),
                            -1, true);
                    itemMaps.add(itemMap);
                }
            }
            case ConstMonster.THAN_LAN_ME -> {
                if (playerTask.getTaskMain().getId() == 8) {
                    if (playerTask.getTaskMain().getIndex() != 1) return;
                    if (Rnd.nextInt(100) < 10) {
                        Item item = ItemFactory.getInstance().createItemNotOptionsBase(ConstItem.NGOC_RONG_7_SAO, player.getId(), 1);
                        ItemMap itemMap = new ItemMap(
                                monster.getArea(),
                                monster.getArea().increaseItemMapID(),
                                player.getId(),
                                item,
                                monster.getX(),
                                360,
                                -1,
                                true
                        );
                        itemMaps.add(itemMap);
                        player.getPlayerTask().checkDoneTask(8, 1);
                    }
                }
            }
        }
    }

    public static void dropMissionItems(Player player) {
        List<ItemDropInfo> itemDropInfos = MAP_ITEM_DROPS.get((short) player.getArea().getMap().getId());
        if (itemDropInfos != null) {
            for (ItemDropInfo itemDropInfo : itemDropInfos) {
                boolean isTaskIdValid = itemDropInfo.taskId() == -1 || player.getPlayerTask().getTaskMain().getId() >= itemDropInfo.taskId();

                int playerTaskIndex = player.getPlayerTask().getTaskMain().getIndex();
                int itemTaskIndex = itemDropInfo.indexTask();

                boolean isIndexTaskValid = itemDropInfo.isTaskStrict() ? (itemTaskIndex == playerTaskIndex) : (itemTaskIndex <= playerTaskIndex);

                if (!isTaskIdValid || !isIndexTaskValid) continue;

                Item item = ItemFactory.getInstance().createItemOptionsBase(itemDropInfo.itemId(), player.getId(), 1);
                player.getPlayerContext().setIdItemTask(player.getArea().increaseItemMapID());
                ItemMap itemMap = new ItemMap(player.getArea(), player.getPlayerContext().getIdItemTask(), player.getId(), item, itemDropInfo.x(), itemDropInfo.y(), -1, false);
                ItemService.getInstance().sendDropItemMap(player, itemMap, false);
            }
        }
    }

    record ItemDropInfo(short itemId, int x, int y, int taskId, int indexTask, boolean isTaskStrict) {
    }

}
