package nro.service.model.map.areas;

import lombok.Setter;
import nro.consts.ConstTypeObject;
import nro.service.core.map.AreaService;
import nro.service.model.entity.BaseModel;
import nro.service.model.map.GameMap;
import nro.service.model.item.ItemMap;
import nro.service.model.entity.monster.Monster;
import nro.service.model.npc.Npc;
import nro.service.model.entity.player.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.Getter;
import nro.server.network.Message;
import nro.server.system.LogServer;
import nro.service.core.item.ItemService;

@Getter
@Setter
@SuppressWarnings("ALL")
public class Area {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final int id;
    private final int maxPlayers;

    private final short MAX_ID = Short.MAX_VALUE - 1;
    private final AtomicInteger idItemMap = new AtomicInteger(0);
    private final GameMap map;
    private Map<Integer, Monster> monsters;

    private final Map<Integer, BaseModel> entitys;

    private final Map<Integer, ItemMap> itemsMap;
    private final List<Npc> npcList;

    public Area(GameMap map, int zoneId, int maxPlayers) {
        this.map = map;
        this.id = zoneId;
        this.maxPlayers = maxPlayers;
        this.entitys = new HashMap<>();
        this.itemsMap = new HashMap<>();
        this.npcList = new ArrayList<>();
    }

    private void updateEntity() {
        this.lock.readLock().lock();
        try {
            for (BaseModel player : this.entitys.values()) {
                player.update();
            }
        } catch (Exception ex) {
            LogServer.LogException("updateLiveObjects: " + ex.getMessage(), ex);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private void updateMonster() {
        this.lock.readLock().lock();
        try {
            for (Monster monster : this.monsters.values()) {
                monster.update();
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

    public void update() {
        try {
            this.updateEntity();
            this.updateMonster();
            this.updateItemMap();
            this.updateNpc();
        } catch (Exception ex) {
            LogServer.LogException("update zone: " + this.id + " message: " + ex.getMessage(), ex);
        }
    }

    public void addPlayer(BaseModel entity) {
        switch (entity) {
            case Player player -> {
                this.lock.writeLock().lock();
                try {
                    if (this.entitys.size() >= this.maxPlayers) {
                        LogServer.LogException("Zone is full: " + this.id);
                        return;
                    }

                    if (this.entitys.containsKey(player.getId())) {
                        AreaService.getInstance().playerExitArea(player);
                    }

                    this.entitys.put(player.getId(), player);
                } catch (Exception ex) {
                    LogServer.LogException("addPlayer: " + ex.getMessage(), ex);
                } finally {
                    this.lock.writeLock().unlock();
                }
            }
            default -> {
                LogServer.LogException("addPlayer: Invalid entity type: " + entity.getTypeObject());
            }
        }
    }

    public void removePlayer(BaseModel entity) {
        this.lock.writeLock().lock();
        try {
            this.entitys.remove(entity.getId());
        } catch (Exception ex) {
            LogServer.LogException(
                    "removePlayer: " + ex.getMessage() + " playerID: " + entity.getId() + " zone id: " + this.id, ex);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public Player getPlayer(int id) {
        this.lock.readLock().lock();
        try {
            BaseModel obj = this.entitys.get(id);
            return (obj instanceof Player player && obj.getTypeObject() == ConstTypeObject.TYPE_PLAYER) ? player : null;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public Collection<Player> getPlayersByType(int typeObject) {
        List<Player> result = new ArrayList<>();
        for (BaseModel obj : this.entitys.values()) {
            if (obj.getTypeObject() == typeObject && obj instanceof Player player) {
                result.add(player);
            }
        }
        return result;
    }


    public Map<Integer, BaseModel> getAllPlayerInZone() {
        this.lock.readLock().lock();
        try {
            return Collections.unmodifiableMap(this.entitys);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public void sendMessageToPlayersInArea(Message message, BaseModel exclude) {
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
            if (itemsMap.size() >= Byte.MAX_VALUE) {
                return;
            }
            this.itemsMap.put(itemMap.getItemMapID(), itemMap);
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

    public Map<Integer, Monster> getMonsters() {
        this.lock.readLock().lock();
        try {
            return this.monsters;
        } finally {
            this.lock.readLock().unlock();
        }
    }

}
