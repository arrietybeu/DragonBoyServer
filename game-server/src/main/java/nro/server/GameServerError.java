package nro.server;

public class GameServerError extends RuntimeException {

    public GameServerError(String message) {
        super(message);
    }

    public GameServerError(String message, Throwable cause) {
        super(message, cause);
    }

}
