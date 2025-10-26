package nro.server.dao;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nro.commons.database.Database;
import nro.server.model.account.Account;
import nro.server.model.account.AccountTime;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arriety
 */
@NoArgsConstructor(access =  AccessLevel.PRIVATE)
public class AccountDAO {

    private static final Logger log = LoggerFactory.getLogger(AccountDAO.class);

    private static final String QUERY_GET_ACCOUNT = "SELECT * FROM `account_data` WHERE `username` = ? AND `password` = ? LIMIT 1;";
    private static int rows;

    public static Account getAccount(String username, String password) {
        return Database.withConnection(con -> {

            AtomicReference<Account> accountRef = new AtomicReference<>();

            Database.select(con, QUERY_GET_ACCOUNT, rs -> {
                if (rs.next()) {
                    Account account = new Account(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                            rs.getBoolean("is_admin"), rs.getBoolean("ban"), rs.getString("ip_address"));
                    accountRef.set(account);
                }
            }, stmt -> {
                stmt.setString(1, username);
                stmt.setString(2, password);
            });

            if (accountRef.get() != null) {
                AccountTime accountTime = getAccountTime(accountRef.get().getId(), con);
                if (accountTime == null)
                    log.error("Account time not found for account id: {}", accountRef.get().getId());
                accountRef.get().setAccountTime(accountTime);
            }

            return accountRef.get();
        });
    }

    public static AccountTime getAccountTime(int accountId, Connection connection) {

        AtomicReference<AccountTime> accountTimeRef = new AtomicReference<>(null);

        Database.select(connection, "SELECT * FROM account_time WHERE account_id = ?", rs -> {
            if (rs.next()) {
                AccountTime accountTime = new AccountTime();
                accountTime.setLastTimeLogin(rs.getTimestamp("last_time_login"));
                accountTime.setLastTimeLogout(rs.getTimestamp("last_time_logout"));
                accountTime.setOfflineTrainingSeconds(rs.getLong("offline_training_seconds"));
                accountTime.setBanUntil(rs.getTimestamp("ban_until"));
                accountTimeRef.set(accountTime);
            }
        }, st -> st.setLong(1, accountId));

        return accountTimeRef.get();
    }

    public static boolean updateAccountTime(int accountId, AccountTime accountTime) {
        var rows = Database.insertUpdate(
                "UPDATE account_time SET last_time_login = ?, last_time_logout = ?, offline_training_seconds = ?, ban_until = ? WHERE account_id = ?",
                ps -> {
                    ps.setTimestamp(1, accountTime.getLastTimeLogin());
                    ps.setTimestamp(2, accountTime.getLastTimeLogout());
                    ps.setLong(3, accountTime.getOfflineTrainingSeconds());
                    ps.setTimestamp(4, accountTime.getBanUntil());
                    ps.setLong(5, accountId);
                }
        );
        System.out.printf(
                "AccountTime update => id: %d, lastLogin: %s, lastLogout: %s, offlineTraining: %d, banUntil: %s%n",
                accountId,
                accountTime.getLastTimeLogin(),
                accountTime.getLastTimeLogout(),
                accountTime.getOfflineTrainingSeconds(),
                accountTime.getBanUntil()
        );

        System.out.println("UPDATE SUCCES : " + rows);
        return rows ;
    }

    public static boolean updateLastIp(int accountId, String ip) {
        return Database.insertUpdate("UPDATE account_data SET ip_address = ? WHERE id = ?", st -> {
            st.setString(1, ip);
            st.setInt(2, accountId);
            st.execute();
        });
    }

}
