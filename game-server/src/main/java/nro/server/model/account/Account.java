package nro.server.model.account;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Arriety
 */

@Getter
@Setter
public class Account {

    private String username;
    private String password;

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
