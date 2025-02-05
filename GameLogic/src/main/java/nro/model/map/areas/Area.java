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
    private final int maxPlayers;
    private final GameMap map;
    private final List<Player> players;
    private final List<Monster> monsters;
    private final List<Npc> npcs;
    private final List<ItemMap> items;

    public Area(GameMap map, int zoneId, int maxPlayers, List<Monster> monsters, List<Npc> npcs) {
        this.map = map;
        this.id = zoneId;
        this.maxPlayers = maxPlayers;
        this.players = new ArrayList<>();
        this.monsters = monsters;
        this.npcs = npcs;
        this.items = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }

}
