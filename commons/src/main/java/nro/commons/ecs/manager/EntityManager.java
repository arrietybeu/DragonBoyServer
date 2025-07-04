package nro.commons.ecs.manager;


import nro.commons.ecs.Entity;
import nro.commons.ecs.World;
import nro.commons.utils.Bag;
import nro.commons.utils.IntDeque;

import java.util.BitSet;

/**
 * Quản lý việc tạo, xóa và vòng đời của thực thể trong khung làm việc ECS.
 * Xử lý việc cấp phát và tái sử dụng ID thực thể để đạt hiệu suất tối ưu.
 *
 * @author Arriety
 */
public class EntityManager {

    private final Bag<Entity> entities;
    private final IntDeque recycledIds;
    private final BitSet activeEntityIds;
    private int nextId = 0;
    private int activeEntityCount = 0;

    /**
     * Tạo một trình quản lý thực thể mới.
     */
    public EntityManager() {
        entities = new Bag<>();
        recycledIds = new IntDeque();
        activeEntityIds = new BitSet();
    }

    /**
     * Tạo một thực thể mới với một ID duy nhất.
     *
     * @param world thế giới mà thực thể thuộc về
     * @return thực thể được tạo
     */
    public Entity createEntity(World world) {
        int id = obtainEntityId();
        Entity entity = createEntityInstance(world, id);

        entities.ensureCapacity(id + 1);
        entities.set(id, entity);
        activeEntityIds.set(id);
        activeEntityCount++;

        return entity;
    }

    /**
     * Tạo một đối tượng thực thể. Gói-riêng tư để truy cập constructor của Entity.
     *
     * @param world thế giới
     * @param id    ID của thực thể
     * @return đối tượng thực thể
     */
    Entity createEntityInstance(World world, int id) {
        return new Entity(world, id);
    }

    /**
     * Xóa một thực thể và tái sử dụng ID của nó.
     *
     * @param entity thực thể cần xóa
     */
    public void deleteEntity(Entity entity) {
        if (entity == null || entity.isDeleted()) {
            return;
        }

        int id = entity.getId();
        entity.setDeleted(true);

        entities.set(id, null);
        activeEntityIds.clear(id);
        activeEntityCount--;

        recycledIds.addLast(id);
    }

    /**
     * Lấy một thực thể bằng ID của nó.
     *
     * @param id ID của thực thể
     * @return thực thể, hoặc null nếu không tìm thấy hoặc đã bị xóa
     */
    public Entity getEntity(int id) {
        Entity entity = entities.get(id);
        return (entity != null && !entity.isDeleted()) ? entity : null;
    }

    /**
     * Kiểm tra xem một thực thể với ID đã cho có đang hoạt động không.
     *
     * @param id ID của thực thể
     * @return true nếu thực thể đang hoạt động, false nếu không
     */
    public boolean isActive(int id) {
        return activeEntityIds.get(id);
    }

    /**
     * Lấy tất cả các thực thể đang hoạt động.
     *
     * @return một túi chứa tất cả các thực thể đang hoạt động
     */
    public Bag<Entity> getAllEntities() {
        Bag<Entity> activeEntities = new Bag<>();
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            if (entity != null && !entity.isDeleted()) {
                activeEntities.add(entity);
            }
        }
        return activeEntities;
    }

    /**
     * Lấy số lượng thực thể đang hoạt động.
     *
     * @return số lượng thực thể đang hoạt động
     */
    public int getActiveEntityCount() {
        return activeEntityCount;
    }

    /**
     * Lấy tổng số thực thể đã từng được tạo (bao gồm cả những thực thể đã bị xóa).
     *
     * @return tổng số thực thể
     */
    public int getTotalEntityCount() {
        return nextId;
    }

    /**
     * Lấy số lượng ID thực thể đã được tái sử dụng.
     *
     * @return số lượng ID đã tái sử dụng
     */
    public int getRecycledIdCount() {
        return recycledIds.size();
    }

    /**
     * Xóa tất cả các thực thể và đặt lại trạng thái của trình quản lý.
     */
    public void clear() {
        entities.clear();
        recycledIds.clear();
        activeEntityIds.clear();
        nextId = 0;
        activeEntityCount = 0;
    }

    /**
     * Lấy một ID thực thể, bằng cách tái sử dụng một ID cũ hoặc tạo một ID mới.
     *
     * @return một ID thực thể
     */
    private int obtainEntityId() {
        if (!recycledIds.isEmpty()) {
            return recycledIds.removeFirst();
        }
        return nextId++;
    }

    /**
     * Lấy thông tin thống kê về trình quản lý thực thể.
     *
     * @return một chuỗi chứa thông tin thống kê của trình quản lý thực thể
     */
    public String getStatistics() {
        return String.format(
                "EntityManager{active=%d, total=%d, recycled=%d, capacity=%d}",
                activeEntityCount, nextId, recycledIds.size(), entities.capacity()
        );
    }

    @Override
    public String toString() {
        return getStatistics();
    }
}