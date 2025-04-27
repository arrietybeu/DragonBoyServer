package nro.login.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class AccountTime {
    /**
     * Time the account has last logged in
     */
    private Timestamp lastLoginTime;
    /**
     * Time after the account will expired
     */
    private Timestamp expirationTime;
    /**
     * Time when the penalty will end
     */
    private Timestamp penaltyEnd;
    /**
     * The duration of the session
     */
    private long sessionDuration;
    /**
     * Accumulated Online Time
     */
    private long accumulatedOnlineTime;
    /**
     * Accumulated Rest Time
     */
    private long accumulatedRestTime;

    /**
     * Default constructor. Set the lastLoginTime to current time
     */
    public AccountTime() {
        this.lastLoginTime = new Timestamp(System.currentTimeMillis());
    }


}
