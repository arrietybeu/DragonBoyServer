package nro.commons.ecs;

import lombok.Getter;

/**
 * @author Arriety
 */
public class WorldConfiguration {

    /**
     * Lấy số lượng thực thể dự kiến.
     */
    @Getter
    private int expectedEntityCount = 100;
    /**
     * Lấy số lượng thành phần dự kiến.
     */
    @Getter
    private int expectedComponentCount = 200;
    private boolean enableEntityCaching = true;
    private boolean enableComponentPooling = false;
    /**
     * Lấy bước thời gian cố định.
     */
    @Getter
    private float fixedTimeStep = 0.016f; // 60 FPS
    private boolean enableProfiling = false;

    /**
     * Tạo một cấu hình thế giới mới với các cài đặt mặc định.
     */
    public WorldConfiguration() {
        // Cấu hình mặc định
    }

    /**
     * Đặt số lượng thực thể dự kiến để tối ưu hóa bộ nhớ.
     *
     * @param expectedEntityCount số lượng thực thể dự kiến
     * @return cấu hình này để xâu chuỗi phương thức
     */

    public WorldConfiguration setExpectedEntityCount(int expectedEntityCount) {
        this.expectedEntityCount = Math.max(1, expectedEntityCount);
        return this;
    }

    /**
     * Đặt số lượng thành phần dự kiến để tối ưu hóa bộ nhớ.
     *
     * @param expectedComponentCount số lượng thành phần dự kiến
     * @return cấu hình này để xâu chuỗi phương thức
     */
    public WorldConfiguration setExpectedComponentCount(int expectedComponentCount) {
        this.expectedComponentCount = Math.max(1, expectedComponentCount);
        return this;
    }

    /**
     * Kích hoạt hoặc vô hiệu hóa bộ nhớ đệm thực thể để cải thiện hiệu suất.
     *
     * @param enableEntityCaching true để kích hoạt bộ nhớ đệm, false để vô hiệu hóa
     * @return cấu hình này để xâu chuỗi phương thức
     */
    public WorldConfiguration setEntityCaching(boolean enableEntityCaching) {
        this.enableEntityCaching = enableEntityCaching;
        return this;
    }

    /**
     * Kiểm tra xem bộ nhớ đệm thực thể có được kích hoạt không.
     *
     * @return true nếu bộ nhớ đệm được kích hoạt, false nếu không
     */
    public boolean isEntityCachingEnabled() {
        return enableEntityCaching;
    }

    /**
     * Kích hoạt hoặc vô hiệu hóa component pooling để tiết kiệm bộ nhớ.
     *
     * @param enableComponentPooling true để kích hoạt pooling, false để vô hiệu hóa
     * @return cấu hình này để xâu chuỗi phương thức
     */
    public WorldConfiguration setComponentPooling(boolean enableComponentPooling) {
        this.enableComponentPooling = enableComponentPooling;
        return this;
    }

    /**
     * Kiểm tra xem component pooling có được kích hoạt không.
     *
     * @return true nếu pooling được kích hoạt, false nếu không
     */
    public boolean isComponentPoolingEnabled() {
        return enableComponentPooling;
    }

    /**
     * Đặt bước thời gian cố định cho các bản cập nhật có tính xác định.
     *
     * @param fixedTimeStep bước thời gian cố định tính bằng giây
     * @return cấu hình này để xâu chuỗi phương thức
     */
    public WorldConfiguration setFixedTimeStep(float fixedTimeStep) {
        this.fixedTimeStep = Math.max(0.001f, fixedTimeStep);
        return this;
    }

    /**
     * Kích hoạt hoặc vô hiệu hóa việc phân tích hiệu suất.
     *
     * @param enableProfiling true để kích hoạt phân tích, false để vô hiệu hóa
     * @return cấu hình này để xâu chuỗi phương thức
     */
    public WorldConfiguration setProfiling(boolean enableProfiling) {
        this.enableProfiling = enableProfiling;
        return this;
    }

    /**
     * Kiểm tra xem việc phân tích hiệu suất có được kích hoạt không.
     *
     * @return true nếu phân tích được kích hoạt, false nếu không
     */
    public boolean isProfilingEnabled() {
        return enableProfiling;
    }

    @Override
    public String toString() {
        return String.format(
                "WorldConfiguration{entities=%d, components=%d, caching=%b, pooling=%b, timeStep=%.3f, profiling=%b}",
                expectedEntityCount, expectedComponentCount, enableEntityCaching,
                enableComponentPooling, fixedTimeStep, enableProfiling
        );
    }
}