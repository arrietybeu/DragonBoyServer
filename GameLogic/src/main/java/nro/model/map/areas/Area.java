package nro.model.map.areas;

import nro.model.map.GameMap;
import nro.model.map.ItemMap;
import nro.model.monster.Monster;
import nro.model.npc.Npc;
import nro.model.player.Player;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class Area {

    private final int id;
    private final GameMap map;
    private final List<Player> players;
    private final List<Monster> monsters;
    private final List<Npc> npcs;
    private final List<ItemMap> items;

    public Area(GameMap map, int zoneId) {
        this.map = map;
        this.id = zoneId;
        this.players = new ArrayList<>();
        this.monsters = new ArrayList<>();
        this.npcs = new ArrayList<>();
        this.items = new ArrayList<>();
    }
}
