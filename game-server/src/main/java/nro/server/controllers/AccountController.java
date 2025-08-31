package nro.server.controllers;

import nro.commons.utils.NetworkUtils;
import nro.server.model.account.Account;
import nro.server.dao.AccountDAO;
import nro.server.network.nro.NroAuthResponse;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.server_packets.handler.SmDialogMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Arriety
 */
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    private static final Map<Integer, NroConnection> accountsOnline = new ConcurrentHashMap<>();

    public static NroAuthResponse Login(String username, String password, NroConnection connection) {

        if (connection.getSessionInfo().isLogin()) return NroAuthResponse.SUCCESS;

        if (BannedIpController.isBanned(connection.getIP())) return NroAuthResponse.IP_BLOCKED;

        // check độ hợp lệ của username và password chuyền từ client
        if (username.isEmpty() || password.isEmpty() || username.equals("1") || password.equals("1")) {
            return NroAuthResponse.INVALID_CREDENTIALS;
        }

        Account account = AccountDAO.getAccount(username, password);
        if (account == null) {
            return NroAuthResponse.ACCOUNT_NOT_FOUND;
        }

        if (account.isBan()) {
            return NroAuthResponse.ACCOUNT_BANNED;
        }

        if (account.getIpForce() != null && !NetworkUtils.checkIPMatching(account.getIpForce(), connection.getIP())) {
            return NroAuthResponse.IP_NOT_ALLOWED;
        }

        synchronized (AccountController.class) {
            log.debug("Account {} logged in from IP {}", account.getUsername(), connection.getIP());
            var con = accountsOnline.remove(account.getId());
            if (con != null) {
                con.close(new SmDialogMessage(NroAuthResponse.ACCOUNT_ALREADY_LOGGED_IN.getCode()));
                return NroAuthResponse.ACCOUNT_ALREADY_LOGGED_IN;
            }

            connection.setAccount(account);
            accountsOnline.put(account.getId(), connection);
        }
        // TODO sau này làm thêm đăng nhập sai quá 5 lần cảnh báo ip bị khóa trong 5p quá 10 lần thì khóa ip vĩnh viễn + username

        return NroAuthResponse.SUCCESS;
    }

    public static void removeAccountOnLS(Account account) {
        accountsOnline.remove(account.getId());
    }


}
