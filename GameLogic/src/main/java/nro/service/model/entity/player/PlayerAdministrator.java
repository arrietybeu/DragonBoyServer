package nro.service.model.entity.player;

import lombok.Getter;
import nro.consts.ConstTypeObject;
import nro.service.model.template.GameNotify;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PlayerAdministrator {

//    private final Player player;
//    private final List<GameNotify> gameNotifies;

    public PlayerAdministrator(Player player) {
//        this.player = player;
//        this.gameNotifies = new ArrayList<>();
//
//        this.initNotify();
    }

//    private void initNotify() {
//        this.gameNotifies.clear();
//        this.createNotify();
//    }
//
//    private void createNotify() {
//        var playerMapSize = this.player.getArea().getPlayersByType(ConstTypeObject.TYPE_PLAYER).size();
//        var itemMapSize = this.player.getArea().getItemsMap().size();
//        var monsterSize = this.player.getArea().getMonsters().size();
//        var npcSize = this.player.getArea().getNpcList().size();
//        var infoArea = "Player Size: " + playerMapSize + "\nitemMapSize: " + itemMapSize + "\nmonsterSize: "
//                + monsterSize + "\nnpcSize: " + npcSize;
//
//        GameNotify gameNotify = new GameNotify((short) 1, "Info Area", infoArea);
//        this.gameNotifies.add(gameNotify);
//    }


}
