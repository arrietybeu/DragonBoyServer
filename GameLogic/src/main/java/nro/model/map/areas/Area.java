package nro.model.map.areas;

import nro.model.map.GameMap;
import nro.model.map.ItemMap;
import nro.model.monster.Monster;
import nro.model.npc.Npc;
import nro.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;

@Getter
public class Area {

    private final int id;
    private final int maxPlayers;
    private final GameMap map;

    private final Map<Integer, Player> players;
    private final Map<Integer, Monster> monsters;
    private final Map<Integer, Npc> npcs;
    private final Map<Integer, ItemMap> items;

    public Area(GameMap map, int zoneId, int maxPlayers, Map<Integer, Monster> monsters, Map<Integer, Npc> npcs) {
        this.map = map;
        this.id = zoneId;
        this.maxPlayers = maxPlayers;
        this.monsters = monsters;
        this.npcs = npcs;
        this.players = new ConcurrentHashMap<>();
        this.items = new ConcurrentHashMap<>();
    }

    public void addPlayer(Player player) {
        this.players.put(player.getId() ,player);
    }

    public void removePlayer(Player player) {
        this.players.remove(player.getId());
    }

}
