package nro.model.map.areas;

import lombok.Setter;
import nro.consts.ConstTypeObject;
import nro.model.LiveObject;
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
    private Map<Integer, Monster> monsters;

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

    private void updateLiveObjects(Collection<? extends LiveObject> objects) {
        this.lock.readLock().lock();
        try {
            for (LiveObject obj : objects) {
                try {
                    obj.update();
                } catch (Exception e) {
                    LogServer.LogException("Error updating object ID: " + obj.getId() + " in zone " + this.id + " - " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("updateLiveObjects: " + ex.getMessage());
            ex.printStackTrace();
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

    public final void update() {
        try {
            updateLiveObjects(this.players.values());
            updateLiveObjects(this.monsters.values());
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
            return (player != null && player.getTypeObject() == ConstTypeObject.TYPE_PLAYER) ? player : null;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public Collection<Player> getPlayersByType(int typeObject) {
        return this.players.values().stream().filter(player -> player.getTypeObject() == typeObject).toList();
    }

    public Map<Integer, Player> getAllPlayerInZone() {
        this.lock.readLock().lock();
        try {
            return Collections.unmodifiableMap(this.players);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public void sendMessageToPlayersInArea(Message message, Player exclude) {
        if (message == null) return;
        this.lock.readLock().lock();
        try {
            this.getPlayersByType(ConstTypeObject.TYPE_PLAYER).forEach(player -> {
                if (exclude == null || player != exclude) {
                    try {
                        player.sendMessage(message);
                    } catch (Exception e) {
                        LogServer.LogException("Error sending message to player ID: " + player.getId() + " in zone " + this.id + " - " + e.getMessage());
                    }
                }
            });
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
            return this.monsters.get(monsterId);
        } catch (Exception ex) {
            LogServer.LogException("getMonsterInAreaById: " + monsterId + " message: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            this.lock.readLock().unlock();
        }
        return null;
    }

    public void addMonster(Monster monster) {
        this.lock.writeLock().lock();
        try {
            this.monsters.put(monster.getId(), monster);
        } catch (Exception ex) {
            LogServer.LogException("addMonster: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void removeMonster(int id) {
        this.lock.writeLock().lock();
        try {
            this.monsters.remove(id);
        } catch (Exception ex) {
            LogServer.LogException("removeMonster: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

}
