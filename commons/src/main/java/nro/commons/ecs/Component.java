package nro.commons.ecs;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Arriety
 */
@Setter
@Getter
public abstract class Component {

    /**
     * Định danh duy nhất cho loại thành phần.
     * Được sử dụng nội bộ bởi khung làm việc ECS để quản lý thành phần hiệu quả.
     * -- GETTER --
     * Gets the component type.
     * <p>
     * <p>
     * -- SETTER --
     * Sets the component type. Used internally by the framework.
     *
     * @return the component type
     * @param type the component type
     */
    private ComponentType type;

    /**
     * Tham chiếu đến thực thể sở hữu thành phần này.
     * -- GETTER --
     * Gets the entity that owns this component.
     * <p>
     * <p>
     * -- SETTER --
     * Sets the owning entity. Used internally by the framework.
     *
     * @return the owning entity
     * @param entity the owning entity
     */
    private Entity entity;

    /**
     * Liệu thành phần này có đang hoạt động hay không.
     * Các thành phần không hoạt động sẽ bị các hệ thống bỏ qua.
     * -- GETTER --
     * Checks if this component is active.
     * <p>
     * <p>
     * -- SETTER --
     * Sets the active state of this component.
     *
     * @return true if active, false otherwise
     * @param active the new active state
     */
    private boolean active = true;

    /**
     * Được gọi khi thành phần được thêm vào một thực thể.
     * Ghi đè phương thức này để thực hiện logic khởi tạo.
     */
    public void onAdded() {
        // Mặc định không làm gì
    }

    /**
     * Được gọi khi thành phần bị xóa khỏi một thực thể.
     * Ghi đè phương thức này để thực hiện logic dọn dẹp.
     */
    public void onRemoved() {
        // Mặc định không làm gì
    }

    /**
     * Đặt lại thành phần về trạng thái mặc định.
     * Ghi đè phương thức này để triển khai component pooling (tái sử dụng thành phần).
     */
    public void reset() {
        active = true;
        entity = null;
        type = null;
    }
}