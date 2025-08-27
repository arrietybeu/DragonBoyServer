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
    private final boolean ban;
    private boolean isAdmin;



    public Account(int id, String username, String password, boolean isAdmin, boolean isBan) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.ban = isBan;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
