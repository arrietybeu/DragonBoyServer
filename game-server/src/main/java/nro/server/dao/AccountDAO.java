package nro.server.dao;

import nro.commons.database.Database;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.server_packets.handler.CmDialogMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Arriety
 */
public class AccountDAO {

    private static final Logger log = LoggerFactory.getLogger(AccountDAO.class);

    private static final String QUERY_GET_ACCOUNT = "SELECT * FROM `account` WHERE `username` = ? AND `password` = ? LIMIT 1;";

    private static boolean getAccount(NroConnection client) {
        var account = client.getAccount();
        AtomicBoolean found = new AtomicBoolean(false);

        Database.select(QUERY_GET_ACCOUNT, rs -> {
            if (rs.next()) {
                found.set(true);
            } else {
                client.sendPacket(new CmDialogMessage("Thông tin đăng nhập không chính xác"));
            }
        }, stmt -> {
            stmt.setString(1, account.getUsername());
            stmt.setString(2, account.getPassword());
        });

        return found.get();
    }
}
