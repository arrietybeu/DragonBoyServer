package nro.commons.ecs;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;
/**
 * @author Arriety
 */
@Getter
public final class ComponentType {

    private static final AtomicInteger typeIndex = new AtomicInteger(0);

    /**
     * Lấy lớp thành phần mà loại này đại diện.
     */
    private final Class<? extends Component> componentClass;
    /**
     * Lấy chỉ số duy nhất của loại thành phần này.
     */
    private final int index;
    /**
     * Lấy tên của loại thành phần này.
     */
    private final String name;

    /**
     * Tạo một loại thành phần mới.
     *
     * @param componentClass lớp thành phần mà loại này đại diện
     */
    public ComponentType(Class<? extends Component> componentClass) {
        this.componentClass = componentClass;
        this.index = typeIndex.getAndIncrement();
        this.name = componentClass.getSimpleName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ComponentType that = (ComponentType) obj;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(index);
    }

    @Override
    public String toString() {
        return "ComponentType{" +
                "name='" + name + '\'' +
                ", index=" + index +
                '}';
    }
}