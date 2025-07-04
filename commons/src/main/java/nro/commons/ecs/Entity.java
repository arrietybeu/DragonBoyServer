package nro.commons.ecs;

import lombok.Getter;
import lombok.Setter;
import nro.commons.utils.Bag;
import nro.commons.utils.BitVector;

/**
 * @author Arriety
 */
@Getter
public class Entity {

    /**
     * Lấy ID duy nhất của thực thể.
     */
    private final int id;
    /**
     * Lấy thế giới mà thực thể này thuộc về.
     */
    private World world;
    /**
     * Lấy tất cả các thành phần trên thực thể này.
     */
    private final Bag<Component> components;
    /**
     * Lấy chuỗi bit thành phần của thực thể này.
     * Được sử dụng nội bộ bởi các hệ thống để so khớp thực thể hiệu quả.
     */
    private final BitVector componentBits;
    /**
     * Đánh dấu thực thể này là đã bị xóa. Được sử dụng nội bộ bởi khung làm việc.
     * Kiểm tra xem thực thể này đã bị xóa chưa.
     */
    @Setter
    private boolean deleted = false;

    /**
     * Tạo một thực thể mới.
     *
     * @param world thế giới mà thực thể này thuộc về
     * @param id    ID duy nhất của thực thể
     */
    public Entity(World world, int id) {
        this.world = world;
        this.id = id;
        this.components = new Bag<>();
        this.componentBits = new BitVector();
    }

    /**
     * Đặt tham chiếu thế giới. Được sử dụng nội bộ bởi khung làm việc.
     *
     * @param world thế giới
     */
    void setWorld(World world) {
        this.world = world;
    }

    /**
     * Thêm một thành phần vào thực thể này.
     *
     * @param component thành phần cần thêm
     * @return thực thể này để xâu chuỗi phương thức
     */
    public Entity addComponent(Component component) {
        if (deleted) {
            throw new IllegalStateException("Không thể thêm thành phần vào thực thể đã bị xóa");
        }

        ComponentType type = ComponentTypeFactory.getTypeFor(component.getClass());
        component.setType(type);
        component.setEntity(this);

        // Đảm bảo túi thành phần đủ lớn
        components.ensureCapacity(type.getIndex() + 1);
        components.set(type.getIndex(), component);
        componentBits.set(type.getIndex());

        component.onAdded();
        world.getComponentManager().addComponent(this, component);

        return this;
    }

    /**
     * Xóa một thành phần khỏi thực thể này.
     *
     * @param componentClass lớp của thành phần cần xóa
     * @return thành phần đã bị xóa, hoặc null nếu không tìm thấy
     */
    public <T extends Component> T removeComponent(Class<T> componentClass) {
        ComponentType type = ComponentTypeFactory.getTypeFor(componentClass);
        return removeComponent(type);
    }

    /**
     * Xóa một thành phần khỏi thực thể này.
     *
     * @param type loại của thành phần cần xóa
     * @return thành phần đã bị xóa, hoặc null nếu không tìm thấy
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> T removeComponent(ComponentType type) {
        if (deleted || !componentBits.get(type.getIndex())) {
            return null;
        }

        T component = (T) components.get(type.getIndex());
        if (component != null) {
            components.set(type.getIndex(), null);
            componentBits.clear(type.getIndex());

            component.onRemoved();
            world.getComponentManager().removeComponent(this, component);
        }

        return component;
    }

    /**
     * Lấy một thành phần từ thực thể này.
     *
     * @param componentClass lớp của thành phần cần lấy
     * @return thành phần, hoặc null nếu không tìm thấy
     */
    public <T extends Component> T getComponent(Class<T> componentClass) {
        ComponentType type = ComponentTypeFactory.getTypeFor(componentClass);
        return getComponent(type);
    }

    /**
     * Lấy một thành phần từ thực thể này.
     *
     * @param type loại của thành phần cần lấy
     * @return thành phần, hoặc null nếu không tìm thấy
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(ComponentType type) {
        if (deleted || !componentBits.get(type.getIndex())) {
            return null;
        }
        return (T) components.get(type.getIndex());
    }

    /**
     * Kiểm tra xem thực thể này có thành phần của loại đã cho không.
     *
     * @param componentClass lớp thành phần cần kiểm tra
     * @return true nếu thành phần tồn tại, false nếu không
     */
    public boolean hasComponent(Class<? extends Component> componentClass) {
        ComponentType type = ComponentTypeFactory.getTypeFor(componentClass);
        return hasComponent(type);
    }

    /**
     * Kiểm tra xem thực thể này có thành phần của loại đã cho không.
     *
     * @param type loại thành phần cần kiểm tra
     * @return true nếu thành phần tồn tại, false nếu không
     */
    public boolean hasComponent(ComponentType type) {
        return !deleted && componentBits.get(type.getIndex());
    }

    /**
     * Xóa thực thể này khỏi thế giới.
     */
    public void delete() {
        world.deleteEntity(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Entity entity = (Entity) obj;
        return id == entity.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "Entity{id=" + id + ", components=" + componentBits.cardinality() + "}";
    }
}