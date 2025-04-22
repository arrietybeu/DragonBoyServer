package nro.commons.services.cron;

public class CronServiceException extends RuntimeException {

    public CronServiceException(String message) {
        super(message);
    }

    public CronServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
