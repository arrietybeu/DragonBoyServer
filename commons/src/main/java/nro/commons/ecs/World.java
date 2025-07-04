package nro.commons.ecs;

import lombok.Getter;
import nro.commons.ecs.manager.ComponentManager;
import nro.commons.ecs.manager.EntityManager;
import nro.commons.utils.Bag;

/**
 * @author Arriety
 */
@Getter
public class World {

    /**
     * Lấy cấu hình của thế giới.
     */
    private final WorldConfiguration configuration;
    /**
     * Lấy trình quản lý thực thể.
     */
    private final EntityManager entityManager;
    /**
     * Lấy trình quản lý thành phần.
     */
    private final ComponentManager componentManager;
    /**
     * Lấy tất cả các hệ thống trong thế giới này.
     */
    private final Bag<BaseSystem> systems;

    /**
     * Kiểm tra xem thế giới đã được khởi tạo chưa.
     */
    private boolean initialized = false;
    /**
     * Lấy thời gian delta hiện tại.
     */
    private float delta = 0f;

    /**
     * Tạo một thế giới mới với cấu hình mặc định.
     */
    public World() {
        this(new WorldConfiguration());
    }

    /**
     * Tạo một thế giới mới với cấu hình được chỉ định.
     *
     * @param configuration cấu hình của thế giới
     */
    public World(WorldConfiguration configuration) {
        this.configuration = configuration;
        this.entityManager = new EntityManager();
        this.componentManager = new ComponentManager();
        this.systems = new Bag<>();
    }

    /**
     * Tạo một thực thể mới trong thế giới này.
     *
     * @return thực thể được tạo
     */
    public Entity createEntity() {
        return entityManager.createEntity(this);
    }

    /**
     * Xóa một thực thể khỏi thế giới này.
     *
     * @param entity thực thể cần xóa
     */
    public void deleteEntity(Entity entity) {
        if (entity == null || entity.isDeleted()) {
            return;
        }

        // Xóa tất cả các thành phần khỏi thực thể
        componentManager.removeAllComponents(entity);

        // Xóa thực thể
        entityManager.deleteEntity(entity);

        // Thông báo cho các hệ thống về việc xóa thực thể
        for (BaseSystem system : systems) {
            if (system.isEnabled()) {
                system.onEntityDeleted(entity);
            }
        }
    }

    /**
     * Lấy một thực thể bằng ID của nó.
     *
     * @param id ID của thực thể
     * @return thực thể, hoặc null nếu không tìm thấy
     */
    public Entity getEntity(int id) {
        return entityManager.getEntity(id);
    }

    /**
     * Lấy tất cả các thực thể trong thế giới này.
     *
     * @return một túi chứa tất cả các thực thể đang hoạt động
     */
    public Bag<Entity> getAllEntities() {
        return entityManager.getAllEntities();
    }

    /**
     * Thêm một hệ thống vào thế giới này.
     *
     * @param system hệ thống cần thêm
     * @return thế giới này để xâu chuỗi phương thức
     */
    public World addSystem(BaseSystem system) {
        system.setWorld(this);
        systems.add(system);

        if (initialized) {
            system.initialize();
        }

        return this;
    }

    /**
     * Xóa một hệ thống khỏi thế giới này.
     *
     * @param system hệ thống cần xóa
     * @return true nếu hệ thống đã được xóa, false nếu không
     */
    public boolean removeSystem(BaseSystem system) {
        if (systems.remove(system)) {
            system.dispose();
            return true;
        }
        return false;
    }

    /**
     * Lấy một hệ thống bằng lớp của nó.
     *
     * @param systemClass lớp của hệ thống
     * @return hệ thống, hoặc null nếu không tìm thấy
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseSystem> T getSystem(Class<T> systemClass) {
        for (BaseSystem system : systems) {
            if (systemClass.isInstance(system)) {
                return (T) system;
            }
        }
        return null;
    }

    /**
     * Khởi tạo thế giới và tất cả các hệ thống của nó.
     * Phải được gọi trước lần cập nhật đầu tiên.
     */
    public void initialize() {
        if (initialized) {
            return;
        }

        // Khởi tạo tất cả các hệ thống
        for (BaseSystem system : systems) {
            system.initialize();
        }

        initialized = true;
    }

    /**
     * Cập nhật thế giới và tất cả các hệ thống của nó.
     *
     * @param deltaTime thời gian đã trôi qua kể từ lần cập nhật cuối cùng tính bằng giây
     */
    public void update(float deltaTime) {
        if (!initialized) {
            throw new IllegalStateException("Thế giới phải được khởi tạo trước khi cập nhật");
        }

        this.delta = deltaTime;

        // Cập nhật tất cả các hệ thống được kích hoạt
        for (BaseSystem system : systems) {
            if (system.isEnabled()) {
                system.update(deltaTime);
            }
        }
    }

    /**
     * Giải phóng thế giới và tất cả các tài nguyên của nó.
     * Nên được gọi khi không còn cần đến thế giới nữa.
     */
    public void dispose() {
        // Giải phóng tất cả các hệ thống
        for (BaseSystem system : systems) {
            system.dispose();
        }
        systems.clear();

        // Xóa các trình quản lý
        componentManager.clear();
        entityManager.clear();

        initialized = false;
    }

    /**
     * Lấy số lượng thực thể đang hoạt động.
     *
     * @return số lượng thực thể đang hoạt động
     */
    public int getEntityCount() {
        return entityManager.getActiveEntityCount();
    }

    /**
     * Lấy số lượng hệ thống.
     *
     * @return số lượng hệ thống
     */
    public int getSystemCount() {
        return systems.size();
    }

    /**
     * Lấy thông tin thống kê về thế giới.
     *
     * @return một chuỗi chứa thông tin thống kê của thế giới
     */
    public String getStatistics() {
        return String.format(
                "World{entities=%d, systems=%d, initialized=%b, %s, %s}",
                getEntityCount(), getSystemCount(), initialized,
                entityManager.getStatistics(), componentManager.getStatistics()
        );
    }

    @Override
    public String toString() {
        return getStatistics();
    }
}