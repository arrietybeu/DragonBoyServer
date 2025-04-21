package nro.server.service.model.map.areas;

import lombok.Setter;
import nro.consts.ConstTypeObject;
import nro.server.service.core.map.AreaService;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.ai.boss.Boss;
import nro.server.service.model.entity.npc.NpcFactory;
import nro.server.service.model.map.GameMap;
import nro.server.service.model.item.ItemMap;
import nro.server.service.model.entity.monster.Monster;
import nro.server.service.model.entity.npc.Npc;
import nro.server.service.model.entity.player.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.Getter;
import nro.server.network.Message;
import nro.server.system.LogServer;

@Getter
@Setter
@SuppressWarnings("ALL")
public class Area {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final int id;
    private final int maxPlayers;

    private static final int UNSET_DELAY = Integer.MIN_VALUE;
    private final short MAX_ID = Short.MAX_VALUE - 1;
    private final AtomicInteger idItemMap = new AtomicInteger(UNSET_DELAY);

    private final GameMap map;
    private Map<Integer, Monster> monsters;
    private final Map<Integer, Entity> entitys;
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

    public void addEntity(Entity entity) {
        this.lock.writeLock().lock();
        try {
            switch (entity) {
                case Player player -> {
                    if (this.getEntitysByType(ConstTypeObject.TYPE_PLAYER).size() >= this.getMaxPlayers()) {
                        LogServer.LogException("Zone is full: " + this.id);
                        return;
                    }
                    if (this.entitys.containsKey(player.getId())) {
                        AreaService.getInstance().playerExitArea(player);
                    }

                    this.entitys.put(player.getId(), player);
                }

                case Boss boss -> {
                    if (this.entitys.containsKey(boss.getId())) {
                        AreaService.getInstance().playerExitArea(boss);
                    }
                    this.entitys.put(boss.getId(), boss);
                }
                default -> {
                    LogServer.LogException("addPlayer: Invalid entity type: " + entity.getTypeObject());
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("addPlayer: " + ex.getMessage(), ex);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void removePlayer(Entity entity) {
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
            Entity obj = this.entitys.get(id);
            return (obj instanceof Player player && obj.getTypeObject() == ConstTypeObject.TYPE_PLAYER) ? player : null;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public Entity getAllEntity(int id) {
        this.lock.readLock().lock();
        try {
            return this.entitys.get(id);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public Collection<Entity> getEntitysByType(int typeObject) {
        List<Entity> result = new ArrayList<>();
        for (Entity obj : this.entitys.values()) {
            switch (obj.getTypeObject()) {
                case ConstTypeObject.TYPE_PLAYER -> {
                    if (obj.getTypeObject() == typeObject && obj instanceof Player player) {
                        result.add(player);
                    }
                }
                case ConstTypeObject.TYPE_BOSS -> {
                    if (obj.getTypeObject() == typeObject && obj instanceof Boss boss) {
                        result.add(boss);
                    }
                }
            }
        }
        return result;
    }

    public Collection<Boss> getBossByType(int typeObject) {
        List<Boss> result = new ArrayList<>();
        for (Entity obj : this.entitys.values()) {
            if (obj.getTypeObject() == typeObject && obj instanceof Boss boss) {
                result.add(boss);
            }
        }
        return result;
    }

    public Map<Integer, Entity> getAllEntityInArea() {
        this.lock.readLock().lock();
        try {
            return Collections.unmodifiableMap(this.entitys);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public void sendMessageToPlayersInArea(Message message, Entity exclude) {
        if (message == null)
            return;
        this.lock.readLock().lock();
        try {
            this.getEntitysByType(ConstTypeObject.TYPE_PLAYER).forEach(entity -> {
                if (exclude == null || entity != exclude) {
                    try {
                        Player player = (Player) entity;
                        player.sendMessage(message);
                    } catch (Exception e) {
                        LogServer.LogException("Error sending message to player ID: " + entity.getId() + " in zone "
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
            this.itemsMap.put(itemMap.getId(), itemMap);
        } catch (Exception ex) {
            LogServer.LogException("addItemMap: " + ex.getMessage()
                    + " itemMapID: " + itemMap.getId(), ex);
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

    public void initNpc() {
        for (var npc : this.map.getNpcs()) {
            Npc npcArea = NpcFactory.createNpc(npc.npcId(), npc.status(), this.map.getId(), npc.x(), npc.y(), npc.avatar());
            if (npcArea == null) continue;
            this.getNpcList().add(npcArea);
        }
    }

    public Area cloneArea(int areaId) {
        Area area = new Area(this.map, areaId, this.maxPlayers);
        area.setMonsters(new HashMap<>(this.monsters));
        area.initNpc();
        return area;
    }

    @Override
    public String toString() {
        return "Area " + id + " players [" + this.getEntitysByType(ConstTypeObject.TYPE_PLAYER).size() + "/" + this.maxPlayers;
    }

}
