package nro.server.realtime.system.item;

import lombok.Getter;
import nro.server.realtime.core.ISystemBase;
import nro.server.service.model.map.areas.Area;

import java.util.Set;

import nro.server.service.model.item.ItemMap;
import nro.server.service.core.item.ItemService;
import nro.server.system.LogServer;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ItemMapSystem implements ISystemBase {

    @Getter
    private static final ItemMapSystem instance = new ItemMapSystem();
    private final List<Area> areas = new ArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void register(Object object) {
        if (object instanceof Area area) {
            lock.writeLock().lock();
            try {
                if (!areas.contains(area)) {
                    areas.add(area);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    @Override
    public void unregister(Object object) {
        if (object instanceof Area area) {
            lock.writeLock().lock();
            try {
                areas.remove(area);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    @Override
    public void update() {
        long now = System.currentTimeMillis();
        lock.writeLock().lock();
        try {
            Iterator<Area> iterator = areas.iterator();
            while (iterator.hasNext()) {
                Area area = iterator.next();

                Map<Integer, ItemMap> itemsMap = area.getItemsMap();
                if (itemsMap.isEmpty()) {
                    iterator.remove();
                    continue;
                }

                updateItemInArea(area, now);
            }
        } catch (Exception e) {
            LogServer.LogException("ItemMapSystem update error: " + e.getMessage(), e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int size() {
        return areas.size();
    }

    private void updateItemInArea(Area area, long currentTime) {
        this.lock.writeLock().lock();
        try {

            Set<Integer> itemsToRemove = new HashSet<>();
            Map<Integer, ItemMap> itemsMap = area.getItemsMap();

            for (ItemMap itemMap : itemsMap.values()) {
                if (itemMap.getItem() == null) {
                    itemsToRemove.add(itemMap.getId());
                    continue;
                }
                System.out.println("Updating items in area: " + area.getId() + " item name:" + itemMap.getItem().getTemplate().name());
                long elapsedTime = currentTime - itemMap.getItem().getCreateTime();
                if (elapsedTime > 60_000) {
                    ItemService.getInstance().sendRemoveItemMap(itemMap);
                    itemsToRemove.add(itemMap.getId());
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

    @Override
    public String name() {
        return "ItemMapSystem";
    }
}
