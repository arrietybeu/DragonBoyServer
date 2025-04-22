package nro.server.service.repositories.account;

import nro.server.config.ConfigDB;
import nro.commons.database.DatabaseFactory;
import nro.server.service.model.template.entity.UserInfo;
import nro.server.manager.UserManager;
import nro.server.service.core.system.ServerService;
import nro.server.manager.SessionManager;
import nro.server.system.LogServer;
import nro.utils.Util;

import java.sql.*;

@SuppressWarnings("ALL")
public class AccountRepository {

    private static final class SingletonHolder {
        private static final AccountRepository instance = new AccountRepository();
    }

    public static AccountRepository getInstance() {
        return AccountRepository.SingletonHolder.instance;
    }

    public boolean handleLogin(UserInfo userInfo) {
        try {
            String username = userInfo.getUsername();
            String password = userInfo.getPassword();
            if (username.isEmpty() || password.isEmpty() || username.equals("1") || password.equals("1")) {
                ServerService.dialogMessage(userInfo.getSession(), "Vui lòng nhập thông tin đăng nhập hợp lệ");
                return false;
            }

            if (this.checkConstLogin(userInfo)) {
                return false;
            }

            String query = "SELECT * FROM `account` WHERE `username` = ? AND `password` = ? LIMIT 1;";
            try (Connection conn = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_DYNAMIC, "login");
                 PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, username);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {
                    // System.out.println("Query: " + ps.toString());
                    if (!rs.next()) {
                        ServerService.dialogMessage(userInfo.getSession(), "Thông tin đăng nhập không chính xác");
                        return false;
                    } else {
                        userInfo.setId(rs.getInt("id"));
                        userInfo.setAdmin(rs.getBoolean("is_admin"));
                        userInfo.setActive(rs.getBoolean("active"));
                        userInfo.setBan(rs.getBoolean("ban"));

                        long lastTimeLogin = rs.getTimestamp("last_time_login").getTime();
                        long lastTimeLogout = rs.getTimestamp("last_time_logout").getTime();

                        userInfo.setLastTimeLogin(lastTimeLogin);
                        userInfo.setLastTimeLogout(lastTimeLogout);
                        this.updateAccount(conn, userInfo);
                    }
                }
            } catch (SQLException e) {
                LogServer.LogException("Error handleLogin: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            LogServer.LogException("Error handleLogin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean checkLogin(UserInfo userInfo) {
        if (userInfo.isBan()) {
            ServerService.dialogMessage(userInfo.getSession(), "Tài khoản đã bị khóa vì có hành vi xấu ảnh hưởng server");
            return true;
        }
        // this.handleCheckTimeLogout(userInfo);
        return false;
    }

    private boolean checkConstLogin(UserInfo userInfo) {
        if (userInfo.getSession().getSessionInfo().constLogin > 10) {
            userInfo.getSession().getSessionInfo().setBanUntil(System.currentTimeMillis() + 3 * 60 * 1000);
            ServerService.dialogMessage(userInfo.getSession(),
                    "Bạn đã đăng nhập sai quá nhiều lần. Vui lòng đợi 3 phút để thử lại.");
            return true;
        }
        return false;
    }

    public void handleCheckTimeLogout(UserInfo userInfo, Runnable callback) {
        try {
            long lastTimeLogin = userInfo.getLastTimeLogin();
            long lastTimeLogout = userInfo.getLastTimeLogout();
            long currentTime = System.currentTimeMillis();

            if ((lastTimeLogin - lastTimeLogout) <= 5000 && (currentTime - lastTimeLogout) < 10000) {
                long waitTime = 10000 - (currentTime - lastTimeLogout);
                long time = waitTime / 1000;

                ServerService.sendLoginDe(userInfo.getSession(), (short) time);

                Util.delay((int) time, () -> {
                    if (userInfo.getSession().isClosed())
                        return;
                    if (handleCheckUserNameOnline(userInfo)) {
                        return;
                    }
                    callback.run();
                });

            } else {
                if (handleCheckUserNameOnline(userInfo)) {
                    return;
                }
                callback.run();
            }
        } catch (Exception e) {
            LogServer.LogException("Error handleCheckTimeLogout: " + e.getMessage(), e);
        }
    }

    public boolean handleCheckUserNameOnline(UserInfo userInfo) {
        try {
            UserInfo pl = UserManager.getInstance().checkUserLogin(userInfo.getUsername());
            if (pl != null) {
                String text = "Tài khoản đã có người đăng nhập xin quay lại sau vài phút";
                ServerService.dialogMessage(pl.getSession(), text);
                ServerService.dialogMessage(userInfo.getSession(), text);

                Util.delay(2, () -> {
                    SessionManager.getInstance().kickSession(userInfo.getSession());
                    SessionManager.getInstance().kickSession(pl.getSession());
                });

                return true;
            }
        } catch (Exception e) {
            LogServer.LogException("Error handleCheckUserNameOnline: " + e.getMessage(), e);
        }
        return false;
    }

    public void updateAccount(Connection con, UserInfo userInfo) throws SQLException {
        String query = "UPDATE account SET ip_address = ?, last_time_login = ? WHERE id = ?;";

        if (con == null) {
            throw new SQLException("Connection is null in updateAccount");
        }

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, userInfo.getSession().getSessionInfo().getIp());
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setInt(3, userInfo.getId());
            ps.executeUpdate();
        }
    }

    public static void updateAccountLogout(UserInfo userInfo) {
        String query = "UPDATE account SET last_time_logout = ? WHERE id = ?;";

        try (Connection con = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_DYNAMIC)) {
            if (con == null) {
                LogServer.LogException("Connection is null in updateAccountLogout");
                return;
            }
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                ps.setInt(2, userInfo.getId());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LogServer.LogException("Error updateAccountLogout: " + e.getMessage(), e);
        }
    }

}
