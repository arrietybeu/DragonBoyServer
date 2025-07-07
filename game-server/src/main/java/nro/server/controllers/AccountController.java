package nro.server.controllers;

import nro.server.model.account.Account;
import nro.server.dao.AccountDAO;
import nro.server.model.account.NroAuthResponse;
import nro.server.network.nro.NroConnection;

/**
 * @author Arriety
 */
public class AccountController {

    public static NroAuthResponse Login(String name, String password, NroConnection connection) {
        if (BannedIpController.isBanned(connection.getIP()))
            return NroAuthResponse.IP_BLOCKED;

        Account account = AccountDAO.getAccount(name, password);
        connection.setAccount(account);
        return NroAuthResponse.SUCCESS;
    }

}
