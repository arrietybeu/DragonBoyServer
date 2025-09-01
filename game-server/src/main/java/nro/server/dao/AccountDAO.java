package nro.server.dao;

import nro.commons.database.Database;
import nro.server.model.account.Account;
import nro.server.model.account.AccountTime;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Arriety
 */
public class AccountDAO {

    private static final String QUERY_GET_ACCOUNT = "SELECT * FROM `account_data` WHERE `username` = ? AND `password` = ? LIMIT 1;";

    public static Account getAccount(String username, String password) {
        return Database.withConnection(con -> {
            AtomicReference<Account> accountRef = new AtomicReference<>();

            Database.select(con, QUERY_GET_ACCOUNT, rs -> {
                if (rs.next()) {
                    Account account = new Account(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getBoolean("is_admin"), rs.getBoolean("ban"), rs.getString("ip_address"));
                    accountRef.set(account);
                }
            }, stmt -> {
                stmt.setString(1, username);
                stmt.setString(2, password);
            });

            if (accountRef.get() != null) {
                AccountTime accountTime = getAccountTime(accountRef.get().getId(), con);
                accountRef.get().setAccountTime(accountTime);
            }

            return accountRef.get();
        });
    }

    public static AccountTime getAccountTime(int accountId, Connection connection) {
        AccountTime accountTime = new AccountTime();

        Database.select(connection, "SELECT * FROM account_time WHERE account_id = ?", rs -> {
            if (rs.next()) {
                accountTime.setLastTimeLogin(rs.getTimestamp("last_time_login"));
                accountTime.setLastTimeLogout(rs.getTimestamp("last_time_logout"));
                accountTime.setOfflineTrainingSeconds(rs.getLong("offline_training_seconds"));
                accountTime.setBanUntil(rs.getTimestamp("ban_until"));
            }
        }, st -> st.setLong(1, accountId));

        return accountTime;
    }

}
