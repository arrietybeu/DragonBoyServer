package nro.server.network.nro;

public enum NroAuthResponse {

    SUCCESS(""),
    INVALID_CREDENTIALS("Vui lòng nhập thông tin đăng nhập hợp lệ"),
    ACCOUNT_BANNED("Tài Khoản của bạn đã bị khóa!"),
    ACCOUNT_NOT_FOUND("Thông tin đăng nhập không chính xác"),
    SERVER_MAINTENANCE("Máy chủ đang bảo trì!"),
    VERSION_MISMATCH("Phiên bản game không tương thích với máy chủ"),
    IP_BLOCKED("IP của bạn đã bị chặn!");

    private final String code;

    NroAuthResponse(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
