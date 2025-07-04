package nro.commons.ecs;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Arriety
 */
public final class ComponentTypeFactory {

    private static final ConcurrentHashMap<Class<? extends Component>, ComponentType> componentTypes = new ConcurrentHashMap<>();

    private ComponentTypeFactory() {
        // Lớp tiện ích
    }

    /**
     * Lấy hoặc tạo một loại thành phần cho một lớp thành phần đã cho.
     *
     * @param componentClass lớp thành phần
     * @return loại thành phần
     */
    public static ComponentType getTypeFor(Class<? extends Component> componentClass) {
        return componentTypes.computeIfAbsent(componentClass, ComponentType::new);
    }

    /**
     * Lấy tổng số lượng các loại thành phần đã được đăng ký.
     *
     * @return số lượng các loại thành phần
     */
    public static int getTypeCount() {
        return componentTypes.size();
    }

    /**
     * Xóa tất cả các loại thành phần đã đăng ký. Sử dụng cẩn thận.
     */
    public static void clear() {
        componentTypes.clear();
    }
}