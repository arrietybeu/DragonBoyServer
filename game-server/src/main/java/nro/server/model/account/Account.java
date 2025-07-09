package nro.server.model.account;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Arriety
 */

@Getter
@Setter
public class Account {

    private final int id;
    private final String username;
    private final String password;

    public Account(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }
}
