package nro.server.network.nro;

public enum NroAuthResponse {

    SUCCESS(0),
    INVALID_CREDENTIALS(1),
    ACCOUNT_BANNED(2),
    ACCOUNT_NOT_FOUND(3),
    SERVER_MAINTENANCE(4),
    VERSION_MISMATCH(5),
    IP_BLOCKED(6);

    private final int code;

    NroAuthResponse(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
