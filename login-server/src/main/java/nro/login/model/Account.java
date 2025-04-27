package nro.login.model;

import java.sql.Timestamp;


public class Account {

    private Integer id;

    private String name;

    private String passwordHash;

    private Timestamp creationDate;

    private byte membership;

    private byte activated;

    private byte lastServer;

    private String lastIp;

    private AccountTime accountTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Account)) {
            return false;
        }

        Account account = (Account) o;

        // noinspection SimplifiableIfStatement
        if (name != null ? !name.equals(account.name) : account.name != null) {
            return false;
        }

        return !(passwordHash != null ? !passwordHash.equals(account.passwordHash) : account.passwordHash != null);
    }
}
