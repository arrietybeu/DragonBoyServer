package nro.model.map.areas;

import lombok.Setter;
import nro.model.map.GameMap;
import nro.model.map.ItemMap;
import nro.model.monster.Monster;
import nro.model.npc.Npc;
import nro.model.player.Player;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.Getter;
import nro.server.network.Message;
import nro.server.LogServer;

@Getter

public class Area {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final int id;
    private final int maxPlayers;
    private final GameMap map;

    @Setter
    private List<Monster> monsters;

    private final Map<Integer, Player> players;

    private final List<Npc> npcList;
    private final List<ItemMap> items;

    public Area(GameMap map, int zoneId, int maxPlayers) {
        this.map = map;
        this.id = zoneId;
        this.maxPlayers = maxPlayers;
        this.players = new HashMap<>();
        this.items = new ArrayList<>();
        this.npcList = new ArrayList<>();
    }

    private void updateMonsters() {
        this.lock.readLock().lock();
        try {
            for (Monster monster : monsters) {
                monster.update();
            }
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private void updatePlayer() {
        this.lock.readLock().lock();
        try {
            for (Player player : this.players.values()) {
                player.update();
            }
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private void updateNpc() {
        this.lock.readLock().lock();
        try {
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private void updateItemMap() {
        this.lock.readLock().lock();
        try {
            for (ItemMap itemMap : this.items) {
                itemMap.update();
            }
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public void update() {
        try {
            this.updatePlayer();
            this.updateMonsters();
            this.updateItemMap();
            this.updateNpc();
        } catch (Exception ex) {
            LogServer.LogException("update zone: " + this.id + " message: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void addPlayer(Player player) {
        this.lock.writeLock().lock();
        try {
            this.players.put(player.getId(), player);
        } catch (Exception ex) {
            LogServer.LogException("addPlayer: " + ex.getMessage());
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
            LogServer.LogException("removePlayer: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public Player getPlayer(int id) {
        this.lock.readLock().lock();
        try {
            Player player = this.players.get(id);
            if (player.getTypeObject() == 1) {
                return player;
            }
        } catch (Exception ex) {
            LogServer.LogException("getPlayer: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            this.lock.readLock().unlock();
        }
        return null;
    }

    public Map<Integer, Player> getAllPlayerInZone() {
        this.lock.readLock().lock();
        try {
            return new HashMap<>(this.players);
        } catch (Exception ex) {
            LogServer.LogException("getAllPlayerInZone: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            this.lock.readLock().unlock();
        }
        return new HashMap<>();
    }

    public void sendMessageToPlayersInArea(Message message, Player exclude) {
        if (message == null) return;
        this.lock.readLock().lock();
        try {
            for (Player player : this.players.values()) {
                if (exclude == null || player != exclude) {
                    player.sendMessage(message);
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("sendMessageToPlayersInArea: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public Npc getNpcById(int npcId) {
        this.lock.readLock().lock();
        try {
            for (Npc npc : this.getNpcList()) {
                if (npc.getTempId() == npcId) {
                    return npc;
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("getNpcById: " + npcId + " message: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            this.lock.readLock().unlock();
        }
        return null;
    }

    public Monster getMonsterInAreaById(int monsterId) {
        this.lock.readLock().lock();
        try {
            for (Monster monster : this.getMonsters()) {
                if (monster.getId() == monsterId) {
                    return monster;
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("getMonsterInAreaById: " + monsterId + " message: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            this.lock.readLock().unlock();
        }
        return null;
    }

}
