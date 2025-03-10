package nro.model.map.areas;

import lombok.Setter;
import nro.consts.ConstItem;
import nro.consts.ConstTypeObject;
import nro.model.LiveObject;
import nro.model.map.GameMap;
import nro.model.item.ItemMap;
import nro.model.monster.Monster;
import nro.model.npc.Npc;
import nro.model.player.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.Getter;
import nro.server.network.Message;
import nro.server.LogServer;
import nro.server.manager.SessionManager;
import nro.service.ItemService;

@Getter
@Setter
@SuppressWarnings("ALL")
public class Area {

    private final int id;
    private final int maxPlayers;

    private final short MAX_ID = Short.MAX_VALUE - 1;
    private final AtomicInteger idItemMap = new AtomicInteger(0);

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final GameMap map;
    private Map<Integer, Monster> monsters;
    private final Map<Integer, Player> players;
    private final Map<Integer, ItemMap> itemsMap;
    private final List<Npc> npcList;

    public Area(GameMap map, int zoneId, int maxPlayers) {
        this.map = map;
        this.id = zoneId;
        this.maxPlayers = maxPlayers;
        this.players = new HashMap<>();
        this.itemsMap = new HashMap<>();
        this.npcList = new ArrayList<>();
    }

    private void updateLiveObjects(Collection<? extends LiveObject> objects) {
        this.lock.readLock().lock();
        try {
            for (LiveObject obj : objects) {
                try {
                    obj.update();
                } catch (Exception e) {
                    LogServer.LogException(
                            "Error updating object ID: " + obj.getId() + " in zone " + this.id + " - " + e.getMessage(),
                            e);
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("updateLiveObjects: " + ex.getMessage(), ex);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private void updateNpc() {
        this.lock.readLock().lock();
        try {
            for (Npc npc : this.npcList) {
                npc.update();
            }
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private void updateItemMap() {
        this.lock.writeLock().lock();
        try {
            long currentTime = System.currentTimeMillis();
            Set<Integer> itemsToRemove = new HashSet<>();

            for (ItemMap itemMap : itemsMap.values()) {
                if (itemMap == null || itemMap.getItem() == null) {
                    itemsToRemove.add(itemMap.getItemMapID());
                    continue;
                }

                long elapsedTime = currentTime - itemMap.getItem().getCreateTime();
                if (elapsedTime > 60_000) {
                    ItemService.getInstance().sendRemoveItemMap(itemMap);
                    itemsToRemove.add(itemMap.getItemMapID());
                } else if (itemMap.getPlayerId() != -1 && elapsedTime > 30_000) {
                    itemMap.setPlayerId(-1);
                }
            }
            itemsToRemove.forEach(itemsMap::remove);
        } catch (Exception ex) {
            LogServer.LogException("updateItemMap: " + ex.getMessage(), ex);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public final void update() {
        try {
            updateLiveObjects(this.players.values());
            updateLiveObjects(this.monsters.values());
            this.updateItemMap();
            this.updateNpc();
        } catch (Exception ex) {
            LogServer.LogException("update zone: " + this.id + " message: " + ex.getMessage(), ex);
        }
    }

    public void addPlayer(Player player) {
        this.lock.writeLock().lock();
        try {
            if (this.players.size() >= this.maxPlayers) {
                LogServer.LogException("Zone is full: " + this.id);
                return;
            }

            if (this.players.containsKey(player.getId())) {
                LogServer.LogException("Player already in zone: " + player.getId());
                SessionManager.getInstance().kickSession(player.getSession());
                return;
            }
            this.players.put(player.getId(), player);
        } catch (Exception ex) {
            LogServer.LogException("addPlayer: " + ex.getMessage(), ex);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void removePlayer(Player player) {
        this.lock.writeLock().lock();
        try {
            this.players.remove(player.getId());
        } catch (Exception ex) {
            LogServer.LogException(
                    "removePlayer: " + ex.getMessage() + " playerID: " + player.getId() + " zone id: " + this.id, ex);
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
        if (message == null)
            return;
        this.lock.readLock().lock();
        try {
            this.getPlayersByType(ConstTypeObject.TYPE_PLAYER).forEach(player -> {
                if (exclude == null || player != exclude) {
                    try {
                        player.sendMessage(message);
                    } catch (Exception e) {
                        LogServer.LogException("Error sending message to player ID: " + player.getId() + " in zone "
                                + this.id + " - " + e.getMessage(), e);
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
            LogServer.LogException("getNpcById: " + npcId + " message: " + ex.getMessage() + " zoneID: " + this.id, ex);
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
            LogServer.LogException(
                    "getMonsterInAreaById: " + monsterId + " message: " + ex.getMessage() + " zoneID: " + this.id, ex);
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
            LogServer.LogException("addMonster: " + ex.getMessage() + " monsterID: " + monster.getId(), ex);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void removeMonster(int id) {
        this.lock.writeLock().lock();
        try {
            this.monsters.remove(id);
        } catch (Exception ex) {
            LogServer.LogException("removeMonster: " + ex.getMessage() + " monsterID: " + id, ex);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public short increaseItemMapID() {
        return (short) idItemMap.updateAndGet(id -> (id >= MAX_ID) ? 0 : id + 1);
    }

    public void addItemMap(ItemMap itemMap) {
        this.lock.writeLock().lock();
        try {

            boolean removed = this.itemsMap.values().removeIf(itemMep -> {
                boolean shouldRemove = itemMep.getItem().getTemplate().id() == ConstItem.DUI_GA_NUONG || itemMep.getItem().getTemplate().id() == ConstItem.DUA_BE;
                if (shouldRemove) {
                    ItemService.getInstance().sendRemoveItemMap(itemMep);
                }
                return shouldRemove;
            });
            this.itemsMap.put(itemMap.getItemMapID(), itemMap);

//            if (removed) {
//                LogServer.DebugLogic("Removed " + this.itemsMap.size() + " items with ID DUI_GA_NUONG or DUA_BE.");
//            }

        } catch (Exception ex) {
            LogServer.LogException("addItemMap: " + ex.getMessage()
                    + " itemMapID: " + itemMap.getItemMapID(), ex);
        } finally {
            this.lock.writeLock().unlock();
        }
    }


    public void removeItemMap(int itemMapID) {
        this.lock.writeLock().lock();
        try {
            this.itemsMap.remove(itemMapID);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public ItemMap getItemsMapById(int itemMapId) {
        this.lock.readLock().lock();
        try {
            return this.itemsMap.get(itemMapId);
        } catch (Exception ex) {
            return null;
        } finally {
            this.lock.readLock().unlock();
        }
    }

}
