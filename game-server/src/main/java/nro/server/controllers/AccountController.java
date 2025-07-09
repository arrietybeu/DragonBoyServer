package nro.server.controllers;

import nro.server.model.account.Account;
import nro.server.dao.AccountDAO;
import nro.server.model.account.NroAuthResponse;
import nro.server.network.nro.NroConnection;

/**
 * @author Arriety
 */
public class AccountController {

    public static NroAuthResponse Login(String username, String password, NroConnection connection) {
        if (BannedIpController.isBanned(connection.getIP()))
            return NroAuthResponse.IP_BLOCKED;

        if (username.isEmpty() || password.isEmpty() || username.equals("1") || password.equals("1")) {
            return NroAuthResponse.INVALID_CREDENTIALS;
        }
        Account account = AccountDAO.getAccount(username, password);
        if (account == null) {
            return NroAuthResponse.ACCOUNT_NOT_FOUND;
        }
        // TODO sau này làm thêm đăng nhập sai quá 5 lần cảnh báo ip bị khóa trong 5p quá 10 lần thì khóa ip vĩnh viễn + username
        connection.setAccount(account);
        return NroAuthResponse.SUCCESS;
    }

}
