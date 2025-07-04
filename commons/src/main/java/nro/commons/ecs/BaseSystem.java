package nro.commons.ecs;


import nro.commons.utils.BitVector;

/**
 * @author Arriety
 */
public abstract class BaseSystem {

    protected World world;
    private boolean enabled = true;
    private boolean initialized = false;
    private final BitVector componentRequirements;
    private final BitVector componentExclusions;

    /**
     * Tạo một hệ thống cơ sở mới.
     */
    public BaseSystem() {
        componentRequirements = new BitVector();
        componentExclusions = new BitVector();
    }

    /**
     * Đặt thế giới mà hệ thống này thuộc về.
     * Được gọi tự động khi hệ thống được thêm vào một thế giới.
     * @param world thế giới
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Lấy thế giới mà hệ thống này thuộc về.
     * @return thế giới
     */
    public World getWorld() {
        return world;
    }

    /**
     * Kiểm tra xem hệ thống này có được kích hoạt không.
     * Các hệ thống bị vô hiệu hóa sẽ không được cập nhật.
     * @return true nếu được kích hoạt, false nếu không
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Kích hoạt hoặc vô hiệu hóa hệ thống này.
     * @param enabled trạng thái kích hoạt
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Kiểm tra xem hệ thống này đã được khởi tạo chưa.
     * @return true nếu đã khởi tạo, false nếu không
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Khởi tạo hệ thống.
     * Được gọi tự động khi hệ thống được thêm vào một thế giới đã khởi tạo.
     * Ghi đè để thực hiện logic khởi tạo.
     */
    public void initialize() {
        if (initialized) {
            return;
        }

        onInitialize();
        initialized = true;
    }

    /**
     * Cập nhật hệ thống.
     * Được gọi mỗi khung hình cho các hệ thống được kích hoạt.
     * @param deltaTime thời gian đã trôi qua kể từ lần cập nhật cuối cùng tính bằng giây
     */
    public void update(float deltaTime) {
        if (!enabled || !initialized) {
            return;
        }

        onUpdate(deltaTime);
    }

    /**
     * Giải phóng hệ thống và các tài nguyên của nó.
     * Được gọi khi hệ thống bị loại bỏ khỏi thế giới hoặc thế giới bị giải phóng.
     */
    public void dispose() {
        if (!initialized) {
            return;
        }

        onDispose();
        initialized = false;
    }

    /**
     * Được gọi khi một thực thể bị xóa khỏi thế giới.
     * Ghi đè để thực hiện dọn dẹp cho dữ liệu cụ thể của thực thể.
     * @param entity thực thể bị xóa
     */
    public void onEntityDeleted(Entity entity) {
        // Mặc định không làm gì
    }

    /**
     * Yêu cầu các thực thể phải có một thành phần của loại được chỉ định.
     * @param componentClass lớp thành phần cần yêu cầu
     * @return hệ thống này để xâu chuỗi phương thức
     */
    protected BaseSystem require(Class<? extends Component> componentClass) {
        ComponentType type = ComponentTypeFactory.getTypeFor(componentClass);
        componentRequirements.set(type.getIndex());
        return this;
    }

    /**
     * Loại trừ các thực thể có một thành phần của loại được chỉ định.
     * @param componentClass lớp thành phần cần loại trừ
     * @return hệ thống này để xâu chuỗi phương thức
     */
    protected BaseSystem exclude(Class<? extends Component> componentClass) {
        ComponentType type = ComponentTypeFactory.getTypeFor(componentClass);
        componentExclusions.set(type.getIndex());
        return this;
    }

    /**
     * Kiểm tra xem một thực thể có khớp với yêu cầu thành phần của hệ thống này không.
     * @param entity thực thể cần kiểm tra
     * @return true nếu thực thể khớp, false nếu không
     */
    protected boolean matches(Entity entity) {
        BitVector entityBits = entity.getComponentBits();

        // Kiểm tra xem thực thể có tất cả các thành phần được yêu cầu không
        if (!entityBits.containsAll(componentRequirements)) {
            return false;
        }

        // Kiểm tra xem thực thể có bất kỳ thành phần bị loại trừ nào không
        if (entityBits.intersects(componentExclusions)) {
            return false;
        }

        return true;
    }

    /**
     * Lấy một thành phần từ một thực thể.
     * Phương thức tiện lợi ủy quyền cho thực thể.
     * @param entity thực thể
     * @param componentClass lớp thành phần
     * @return thành phần, hoặc null nếu không tìm thấy
     */
    protected <T extends Component> T getComponent(Entity entity, Class<T> componentClass) {
        return entity.getComponent(componentClass);
    }

    /**
     * Được gọi trong quá trình khởi tạo hệ thống.
     * Ghi đè để thực hiện logic khởi tạo tùy chỉnh.
     */
    protected void onInitialize() {
        // Mặc định không làm gì
    }

    /**
     * Được gọi trong quá trình cập nhật hệ thống.
     * Ghi đè để thực hiện logic hệ thống.
     * @param deltaTime thời gian đã trôi qua kể từ lần cập nhật cuối cùng tính bằng giây
     */
    protected abstract void onUpdate(float deltaTime);

    /**
     * Được gọi trong quá trình giải phóng hệ thống.
     * Ghi đè để thực hiện logic dọn dẹp tùy chỉnh.
     */
    protected void onDispose() {
        // Mặc định không làm gì
    }

    @Override
    public String toString() {
        return String.format(
                "%s{enabled=%b, initialized=%b, requirements=%d, exclusions=%d}",
                getClass().getSimpleName(), enabled, initialized,
                componentRequirements.cardinality(), componentExclusions.cardinality()
        );
    }
}