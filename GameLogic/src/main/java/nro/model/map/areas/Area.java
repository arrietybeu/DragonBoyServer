package nro.model.map.areas;

import nro.model.map.GameMap;
import nro.model.map.ItemMap;
import nro.model.map.Waypoint;
import nro.model.monster.Monster;
import nro.model.npc.Npc;
import nro.model.player.Player;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.Getter;
import nro.network.Message;

@Getter
public class Area {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final int id;
    private final int maxPlayers;
    private final GameMap map;

    private final Map<Integer, Player> players;
    private final List<Monster> monsters;
    private final List<Npc> npcList;
    private final List<ItemMap> items;

    public Area(GameMap map, int zoneId, int maxPlayers, List<Monster> monsters, List<Npc> npcList) {
        this.map = map;
        this.id = zoneId;
        this.maxPlayers = maxPlayers;
        this.monsters = monsters;
        this.npcList = npcList;
        this.players = new HashMap<>();
        this.items = new ArrayList<>();
    }

    public void sendMsgAllPlayerInZone(Message message) {
        if (message == null) return;
        this.lock.readLock().lock();
        try {
            for (Player player : this.players.values()) {
                if (player != null) {
                    player.sendMessage(message);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public void addPlayer(Player player) {
        this.lock.writeLock().lock();
        try {
            this.players.put(player.getId(), player);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void removePlayer(Player player) {
        this.lock.writeLock().lock();
        try {
            this.players.remove(player.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public Player getPlayer(int id) {
        this.lock.readLock().lock();
        try {
            return this.players.get(id);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            this.lock.readLock().unlock();
        }
        return null;
    }

    public Map<Integer, Player> getAllPlayerInZone() {
        this.lock.readLock().lock();
        try {
            return this.players;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            this.lock.readLock().unlock();
        }
        return null;
    }
}
