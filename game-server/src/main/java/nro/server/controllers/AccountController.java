package nro.server.controllers;

import nro.commons.utils.NetworkUtils;
import nro.server.model.account.Account;
import nro.server.dao.AccountDAO;
import nro.server.network.nro.NroAuthResponse;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.server_packets.handler.SmDialogMessage;
import nro.server.network.sequrity.LoginThrottle;
import nro.server.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Arriety
 */
public class AccountController {

    public static final Map<Integer, NroConnection> accountsOnline = new ConcurrentHashMap<>();

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    public static NroAuthResponse Login(String username, String password, NroConnection connection) {

        var ip = connection.getIP();
        
        if (BannedIpController.isBanned(ip)) return NroAuthResponse.IP_BLOCKED;

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
            LoginThrottle.onLoginFail(ip);
            return NroAuthResponse.ACCOUNT_ALREADY_LOGGED_IN;
        }

        if (account.isBan()) {
            LoginThrottle.onLoginFail(ip);
            return NroAuthResponse.ACCOUNT_BANNED;
        }

//        if (account.getIpForce() != null && !NetworkUtils.checkIPMatching(account.getIpForce(), ip)) {
//            LoginThrottle.onLoginFail(ip);
//            return NroAuthResponse.IP_NOT_ALLOWED;
//        }

        synchronized (AccountController.class) {
            log.debug("Account {} logged in from IP {}", account.getUsername(), ip);
            var con = accountsOnline.remove(account.getId());
            if (con != null) {
                LoginThrottle.onLoginFail(ip);
                return NroAuthResponse.ACCOUNT_ALREADY_LOGGED_IN;
            }

            connection.setAccount(account);

            accountsOnline.put(account.getId(), connection);
        }
        LoginThrottle.onLoginSuccess(ip);

        return NroAuthResponse.SUCCESS;
    }

    public static void removeAccountOnLS(Account account) {
        accountsOnline.remove(account.getId());
    }


}
