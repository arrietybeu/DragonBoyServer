package nro.server.dao;

import nro.commons.database.Database;
import nro.server.model.account.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Arriety
 */
public class AccountDAO {

    private static final Logger log = LoggerFactory.getLogger(AccountDAO.class);

    private static final String QUERY_GET_ACCOUNT = "SELECT * FROM `account_data` WHERE `username` = ? AND `password` = ? LIMIT 1;";

    public static Account getAccount(String username, String password) {

        AtomicReference<Account> accountRef = new AtomicReference<>();

        Database.select(QUERY_GET_ACCOUNT, rs -> {
            if (rs.next()) {
                Account account = new Account(rs.getString("username"), rs.getString("password"));
                accountRef.set(account);
            }
        }, stmt -> {
            stmt.setString(1, username);
            stmt.setString(2, password);
        });

        return accountRef.get();
    }
}
