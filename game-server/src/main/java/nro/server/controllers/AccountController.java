package nro.server.controllers;

import nro.server.model.account.Account;
import nro.server.model.account.AccountTime;
import nro.server.dao.AccountDAO;
import nro.server.network.nro.NroAuthResponse;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.server_packets.handler.SmDialogMessage;
import nro.server.network.nro.server_packets.handler.SmLoginDelay;
import nro.server.network.sequrity.LoginThrottle;
import nro.server.utils.ThreadPoolManager;
import nro.server.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Arriety
 */
public class AccountController {

    public static final Map<Integer, NroConnection> accountsOnline = new ConcurrentHashMap<>();

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    public static NroAuthResponse Login(String username, String password, NroConnection connection) {

        var ip = connection.getIP();

        if (BannedIpController.isBanned(ip))
            return NroAuthResponse.IP_BLOCKED;

        long remainMs = LoginThrottle.getRemainingMs(ip);
        if (remainMs > 0) {
            String msg = "Bạn đã nhập sai quá nhiều lần. "
                    + "Vui lòng đợi " + Utils.humanize(remainMs) + " để đăng nhập lại.";
            connection.sendPacket(new SmDialogMessage(msg));
            return NroAuthResponse.RELOGIN;
        }

        // check độ hợp lệ của username và password chuyền từ client
        if (username.isEmpty() || password.isEmpty() || username.equals("1") || password.equals("1"))
            return NroAuthResponse.INVALID_CREDENTIALS;

        Account account = AccountDAO.getAccount(username, password);
        if (account == null) {
            LoginThrottle.onLoginFail(ip);
            return NroAuthResponse.ACCOUNT_NOT_FOUND;
        }

        if (accountsOnline.containsKey(account.getId())) {
            NroConnection con = accountsOnline.get(account.getId());

            con.close(new SmDialogMessage("Tài khoản này đã được đăng nhập ở 1 nơi khác"));

            LoginThrottle.onLoginFail(ip);
            return NroAuthResponse.ACCOUNT_ALREADY_LOGGED_IN;
        }

        if (account.isBan()) {
            LoginThrottle.onLoginFail(ip);
            return NroAuthResponse.ACCOUNT_BANNED;
        }

        AccountTime accountTime = account.getAccountTime();

        long lastTimeLogin = accountTime.getLastTimeLogin().getTime();
        long lastTimeLogout = accountTime.getLastTimeLogout().getTime();
        long currentTime = System.currentTimeMillis();

        if ((lastTimeLogin - lastTimeLogout) <= 5000 && (currentTime - lastTimeLogout) < 10000) {
            long waitTime = 10000 - (currentTime - lastTimeLogout);
            short time = (short) (waitTime / 1000);
            connection.sendPacket(new SmLoginDelay(time));

            ThreadPoolManager.getInstance().schedule("resume-login-" + username, () -> {
                if (connection == null)
                    return;

                Login(username, password, connection);
            }, time, TimeUnit.SECONDS);

            return NroAuthResponse.LOGIN_DELAY;
        }

        // if (account.getIpForce() != null &&
        // !NetworkUtils.checkIPMatching(account.getIpForce(), ip)) {
        // LoginThrottle.onLoginFail(ip);
        // return NroAuthResponse.IP_NOT_ALLOWED;
        // }

        synchronized (AccountController.class) {
            log.debug("Account {} logged in from IP {}", account.getUsername(), ip);
            var con = accountsOnline.remove(account.getId());
            if (con != null) {
                LoginThrottle.onLoginFail(ip);
                con.close(new SmDialogMessage("Tài khoản này đã được đăng nhập ở 1 nơi khác"));
                return NroAuthResponse.ACCOUNT_ALREADY_LOGGED_IN;
            }

            connection.setAccount(account);

            accountsOnline.put(account.getId(), connection);
        }

        LoginThrottle.onLoginSuccess(ip);
        updateOnLogin(account);

        return NroAuthResponse.SUCCESS;
    }

    public static void removeAccountOnLS(Account account) {
        accountsOnline.remove(account.getId());
    }

    public static void updateOnLogin(Account account) {
        AccountTime accountTime = account.getAccountTime();

        int lastLoginDay = getDays(accountTime.getLastTimeLogin().getTime());
        int currentDay = getDays(System.currentTimeMillis());

        // nếu ngày đăng nhập khác ngày hiện tại thì reset thời gian offline
        if (lastLoginDay < currentDay) {
            accountTime.setOfflineTrainingSeconds(0);
        } else {
            long restTime = System.currentTimeMillis() - accountTime.getLastTimeLogin().getTime()
                    - accountTime.getOfflineTrainingSeconds();
            accountTime.setOfflineTrainingSeconds(accountTime.getOfflineTrainingSeconds() + restTime);
        }

        accountTime.setLastTimeLogin(new Timestamp(System.currentTimeMillis()));

        AccountDAO.updateAccountTime(account.getId(), accountTime);

        account.setAccountTime(accountTime);
    }

    public static void updateOnLogout(Account account) {
        AccountTime accountTime = account.getAccountTime();

        accountTime.setLastTimeLogout(new Timestamp(System.currentTimeMillis()));

        AccountDAO.updateAccountTime(account.getId(), accountTime);
        account.setAccountTime(accountTime);
    }

    public static void kickAccount(int accountId, String... message) {
        synchronized (AccountController.class) {

            NroConnection conn = accountsOnline.remove(accountId);
            if (conn != null)
                conn.close(new SmDialogMessage(
                        message.length > 0 ? message[0] : "Tôi buồn lắm :("));
        }
    }

    /**
     * Get days from time presented in milliseconds
     * 
     * @param millis
     *               time in ms
     * @return days
     */
    public static int getDays(long millis) {
        return (int) (millis / 1000 / 3600 / 24);
    }

}
