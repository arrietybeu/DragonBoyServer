package nro.server.model.account;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author Arriety
 */
@Getter
@Setter
public class AccountTime {

    private Timestamp lastTimeLogin;
    private Timestamp lastTimeLogout;
    private Timestamp banUntil;
    private long offlineTrainingSeconds;

    public AccountTime() {
        this.lastTimeLogin = new Timestamp(System.currentTimeMillis());
    }
}
