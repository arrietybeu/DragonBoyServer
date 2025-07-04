package nro.commons.ecs;


import nro.commons.utils.Bag;

/**
 * Một hệ thống trừu tượng tự động lặp qua tất cả các thực thể phù hợp
 * và gọi phương thức process cho mỗi thực thể trong quá trình cập nhật.
 *
 * Các lớp con nên triển khai phương thức process để định nghĩa logic của chúng
 * và sử dụng các phương thức require/exclude để chỉ định những thực thể nào cần xử lý.
 *
 * @author Arriety
 */
public abstract class IteratingSystem extends BaseSystem {

    /**
     * Tạo một hệ thống lặp mới.
     */
    public IteratingSystem() {
        super();
    }

    /**
     * Cập nhật hệ thống bằng cách lặp qua tất cả các thực thể phù hợp
     * và gọi phương thức process cho mỗi thực thể.
     *
     * @param deltaTime thời gian đã trôi qua kể từ lần cập nhật cuối cùng tính bằng giây
     */
    @Override
    protected final void onUpdate(float deltaTime) {
        if (world == null) {
            return;
        }

        // Lấy tất cả các thực thể từ thế giới
        Bag<Entity> entities = world.getEntityManager().getAllEntities();

        // Lặp qua tất cả các thực thể và xử lý những thực thể phù hợp
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);

            // Bỏ qua các thực thể null, đã bị xóa, hoặc không khớp với yêu cầu của chúng ta
            if (entity == null || entity.isDeleted() || !matches(entity)) {
                continue;
            }

            // Xử lý thực thể phù hợp
            process(entity, deltaTime);
        }
    }

    /**
     * Xử lý một thực thể duy nhất.
     * Phương thức này được gọi một lần mỗi khung hình cho mỗi thực thể khớp
     * với yêu cầu và loại trừ thành phần của hệ thống.
     *
     * @param entity thực thể cần xử lý
     * @param deltaTime thời gian đã trôi qua kể từ lần cập nhật cuối cùng tính bằng giây
     */
    protected abstract void process(Entity entity, float deltaTime);
}