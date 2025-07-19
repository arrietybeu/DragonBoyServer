package nro.server.network.nro;

import lombok.Getter;

/**
 * @Author Arriety
 */
@Getter
public enum PlayerResponseType {

    // COMMON RESPONSES
    SUCCESS(0, ""),

    CREATION_FAILED_NAME_INVALID(10, "Tên nhân vật không hợp lệ (phải từ 5-15 ký tự và không chứa ký tự đặc biệt)."),

    CREATION_FAILED_NAME_TAKEN(11, "Tên nhân vật này đã có người sử dụng. Vui lòng chọn tên khác."),

    CREATION_FAILED_ACCOUNT_HAS_CHAR(12, "Mỗi tài khoản chỉ được tạo một nhân vật duy nhất."),

    CREATION_FAILED_GENDER_INVALID(13, "Chọn giới tính không hợp lệ. Vui lòng chọn lại."),

    CREATION_FAILED_HAIR_INVALID(14, "Kiểu tóc không hợp lệ cho giới tính đã chọn. Vui lòng chọn lại."),

    CREATION_FAILED_SERVER_ERROR(19, "Không thể tạo nhân vật do lỗi từ máy chủ. Vui lòng thử lại sau."),

    //================================================================
    // TRẠNG THÁI TẢI DỮ LIỆU KHI PLAYER (Mã 20 -> 29)
    //================================================================
    LOGIN_SUCCESS(20, ""),

    LOGIN_FAILED_DATA_LOAD_ERROR(21, "Xảy ra lỗi trong quá trình tải dữ liệu nhân vật. Vui lòng thử lại."),

    LOGIN_FAILED_SERVER_FULL(23, "Máy chủ hiện đã đầy. Vui lòng thử lại sau ít phút."),

    LOGIN_FAILED_UNKNOWN_ERROR(24, "Đã xảy ra lỗi không xác định. Vui lòng liên hệ hỗ trợ.");

    private final int code;
    private final String defaultMessage;

    PlayerResponseType(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

}
