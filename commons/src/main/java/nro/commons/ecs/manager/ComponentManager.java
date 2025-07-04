package nro.commons.ecs.manager;

import nro.commons.ecs.Component;
import nro.commons.ecs.ComponentType;
import nro.commons.ecs.ComponentTypeFactory;
import nro.commons.ecs.Entity;
import nro.commons.utils.Bag;

import java.util.concurrent.ConcurrentHashMap;
/**
 * Quản lý việc lưu trữ và truy xuất thành phần cho các thực thể trong khung làm việc ECS.
 * Cung cấp quyền truy cập hiệu quả vào các thành phần theo loại và thực thể.
 *
 * @author Arriety
 */
public class ComponentManager {

    private final ConcurrentHashMap<ComponentType, Bag<Component>> componentsByType;
    private final Bag<Bag<Component>> componentsByEntity;

    /**
     * Tạo một trình quản lý thành phần mới.
     */
    public ComponentManager() {
        componentsByType = new ConcurrentHashMap<>();
        componentsByEntity = new Bag<>();
    }

    /**
     * Thêm một thành phần vào một thực thể.
     * @param entity thực thể để thêm thành phần vào
     * @param component thành phần cần thêm
     */
    public void addComponent(Entity entity, Component component) {
        ComponentType type = component.getType();
        int entityId = entity.getId();

        // Thêm vào bộ lưu trữ dựa trên loại
        Bag<Component> componentsOfType = componentsByType.computeIfAbsent(
                type, k -> new Bag<>()
        );

        // Đảm bảo túi có thể chứa thành phần của thực thể này
        componentsOfType.ensureCapacity(entityId + 1);
        componentsOfType.set(entityId, component);

        // Thêm vào bộ lưu trữ dựa trên thực thể
        componentsByEntity.ensureCapacity(entityId + 1);
        Bag<Component> entityComponents = componentsByEntity.get(entityId);
        if (entityComponents == null) {
            entityComponents = new Bag<>();
            componentsByEntity.set(entityId, entityComponents);
        }
        entityComponents.add(component);
    }

    /**
     * Xóa một thành phần khỏi một thực thể.
     * @param entity thực thể để xóa thành phần khỏi
     * @param component thành phần cần xóa
     */
    public void removeComponent(Entity entity, Component component) {
        ComponentType type = component.getType();
        int entityId = entity.getId();

        // Xóa khỏi bộ lưu trữ dựa trên loại
        Bag<Component> componentsOfType = componentsByType.get(type);
        if (componentsOfType != null) {
            componentsOfType.set(entityId, null);
        }

        // Xóa khỏi bộ lưu trữ dựa trên thực thể
        Bag<Component> entityComponents = componentsByEntity.get(entityId);
        if (entityComponents != null) {
            entityComponents.remove(component);
        }
    }

    /**
     * Lấy một thành phần từ một thực thể theo loại.
     * @param entity thực thể để lấy thành phần từ
     * @param componentClass lớp của thành phần cần lấy
     * @return thành phần, hoặc null nếu không tìm thấy
     */
    public <T extends Component> T getComponent(Entity entity, Class<T> componentClass) {
        ComponentType type = ComponentTypeFactory.getTypeFor(componentClass);
        return getComponent(entity, type);
    }

    /**
     * Lấy một thành phần từ một thực thể theo loại.
     * @param entity thực thể để lấy thành phần từ
     * @param type loại của thành phần cần lấy
     * @return thành phần, hoặc null nếu không tìm thấy
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Entity entity, ComponentType type) {
        Bag<Component> componentsOfType = componentsByType.get(type);
        if (componentsOfType == null) {
            return null;
        }
        return (T) componentsOfType.get(entity.getId());
    }

    /**
     * Lấy tất cả các thành phần của một loại cụ thể.
     * @param componentClass lớp thành phần
     * @return một túi chứa tất cả các thành phần của loại được chỉ định
     */
    public <T extends Component> Bag<T> getComponentsOfType(Class<T> componentClass) {
        ComponentType type = ComponentTypeFactory.getTypeFor(componentClass);
        return getComponentsOfType(type);
    }

    /**
     * Lấy tất cả các thành phần của một loại cụ thể.
     * @param type loại thành phần
     * @return một túi chứa tất cả các thành phần của loại được chỉ định
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> Bag<T> getComponentsOfType(ComponentType type) {
        Bag<Component> components = componentsByType.get(type);
        if (components == null) {
            return new Bag<>();
        }

        Bag<T> result = new Bag<>();
        for (Component component : components) {
            if (component != null && component.isActive()) {
                result.add((T) component);
            }
        }
        return result;
    }

    /**
     * Lấy tất cả các thành phần được gắn vào một thực thể.
     * @param entity thực thể
     * @return một túi chứa tất cả các thành phần trên thực thể
     */
    public Bag<Component> getComponents(Entity entity) {
        Bag<Component> entityComponents = componentsByEntity.get(entity.getId());
        return entityComponents != null ? entityComponents : new Bag<>();
    }

    /**
     * Kiểm tra xem một thực thể có thành phần của loại được chỉ định không.
     * @param entity thực thể cần kiểm tra
     * @param componentClass lớp thành phần cần kiểm tra
     * @return true nếu thực thể có thành phần, false nếu không
     */
    public boolean hasComponent(Entity entity, Class<? extends Component> componentClass) {
        ComponentType type = ComponentTypeFactory.getTypeFor(componentClass);
        return hasComponent(entity, type);
    }

    /**
     * Kiểm tra xem một thực thể có thành phần của loại được chỉ định không.
     * @param entity thực thể cần kiểm tra
     * @param type loại thành phần cần kiểm tra
     * @return true nếu thực thể có thành phần, false nếu không
     */
    public boolean hasComponent(Entity entity, ComponentType type) {
        Bag<Component> componentsOfType = componentsByType.get(type);
        if (componentsOfType == null) {
            return false;
        }
        Component component = componentsOfType.get(entity.getId());
        return component != null && component.isActive();
    }

    /**
     * Xóa tất cả các thành phần khỏi một thực thể.
     * @param entity thực thể cần xóa thành phần
     */
    public void removeAllComponents(Entity entity) {
        int entityId = entity.getId();

        // Xóa khỏi bộ lưu trữ dựa trên loại
        for (Bag<Component> components : componentsByType.values()) {
            components.set(entityId, null);
        }

        // Xóa bộ lưu trữ dựa trên thực thể
        Bag<Component> entityComponents = componentsByEntity.get(entityId);
        if (entityComponents != null) {
            entityComponents.clear();
        }
    }

    /**
     * Lấy số lượng các loại thành phần hiện đã được đăng ký.
     * @return số lượng các loại thành phần
     */
    public int getComponentTypeCount() {
        return componentsByType.size();
    }

    /**
     * Lấy tổng số lượng các phiên bản thành phần.
     * @return tổng số lượng thành phần
     */
    public int getTotalComponentCount() {
        int count = 0;
        for (Bag<Component> components : componentsByType.values()) {
            for (Component component : components) {
                if (component != null && component.isActive()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Xóa tất cả các thành phần và đặt lại trạng thái của trình quản lý.
     */
    public void clear() {
        componentsByType.clear();
        componentsByEntity.clear();
    }

    /**
     * Lấy thông tin thống kê về trình quản lý thành phần.
     * @return một chuỗi chứa thông tin thống kê của trình quản lý thành phần
     */
    public String getStatistics() {
        return String.format(
                "ComponentManager{types=%d, totalComponents=%d}",
                getComponentTypeCount(), getTotalComponentCount()
        );
    }

    @Override
    public String toString() {
        return getStatistics();
    }
}