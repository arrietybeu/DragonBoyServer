package nro.service.core;

import lombok.Getter;
import nro.model.item.Item;
import nro.model.item.ItemMap;
import nro.model.monster.Monster;
import nro.model.player.Player;
import nro.model.player.PlayerTask;

import java.util.ArrayList;
import java.util.List;

public class DropItemMap {

    @Getter
    private static final DropItemMap instance = new DropItemMap();

    public List<ItemMap> dropItemMapForMonster(Player player, Monster monster) {
        List<ItemMap> itemMaps = new ArrayList<>();

        this.dropItemMapToTask(player, monster, itemMaps);
        return itemMaps;
    }

    private void dropItemMapToTask(Player player, Monster monster, List<ItemMap> itemMaps) {
        PlayerTask playerTask = player.getPlayerTask();
        switch (playerTask.getTaskMain().getId()) {
            case 2 -> {
                Item item = ItemFactory.getInstance().createItemNotOptionsBase(73, 1);
                ItemMap itemMap = new ItemMap(monster.getArea(), player.getId(), item, monster.getX(), monster.getY(), -1);
                itemMaps.add(itemMap);
            }
        }
    }

}
