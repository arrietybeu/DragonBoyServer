package nro.repositories.account;

import nro.server.config.ConfigDB;
import nro.repositories.DatabaseConnectionPool;
import nro.model.template.entity.UserInfo;
import nro.server.manager.UserManager;
import nro.service.Service;
import nro.server.manager.SessionManager;
import nro.server.LogServer;

import java.sql.*;

@SuppressWarnings("ALL")
public class AccountRepository {

    private static AccountRepository instance;

    public static AccountRepository getInstance() {
        if (instance == null) {
            instance = new AccountRepository();
        }
        return instance;
    }

    public boolean checkAccount(UserInfo userInfo) {
        try {
            String username = userInfo.getUsername();
            String password = userInfo.getPassword();

            if (username.isEmpty() || password.isEmpty() || username.equals("1") || password.equals("1")) {
                Service.dialogMessage(userInfo.getSession(), "Vui lòng nhập thông tin đăng nhập hợp lệ");
                return false;
            }

            String query = "SELECT * FROM `account` WHERE `username` = ? AND `password` = ? LIMIT 1;";

            try (Connection conn = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_DYNAMIC, "login");
                 PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, username);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        Service.dialogMessage(userInfo.getSession(), "Thông tin đăng nhập không chính xác");
                        return false;
                    } else {
                        userInfo.setId(rs.getInt("id"));

//                        UserInfo plOnline = UserManager.getInstance().get(userInfo.getId());
//                        System.out.println("plOnline: " + plOnline);
//                        if (plOnline != null) {
//                            Service.dialogMessage(plOnline.getSession(), "Bạn hiện tại không thể vào Account\nkhi có người đang trong Account của bạn");
//                            Service.dialogMessage(userInfo.getSession(), "Bạn hiện tại không thể vào Account\nkhi có người đang trong Account của bạn");
//                            try {
//                                Thread.sleep(1000);
//                            } catch (Exception ignored) {
//                            }
//                            return false;
//                        }

                        userInfo.setAdmin(rs.getBoolean("is_admin"));
                        userInfo.setActive(rs.getBoolean("active"));
                        userInfo.setLastTimeLogin(rs.getTimestamp("last_time_login").getTime());
                        userInfo.setLastTimeLogout(rs.getTimestamp("last_time_logout").getTime());
                        userInfo.setBan(rs.getBoolean("ban"));

                        if (userInfo.isBan()) {
                            Service.dialogMessage(userInfo.getSession(), "Tài khoản đã bị khóa vì có hành vi xấu ảnh hưởng server");
                            return false;
                        }
                    }
                }
                updateAccount(conn, userInfo);
            } catch (Exception e) {
                LogServer.LogException("Error checkAccount: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            LogServer.LogException("Error checkAccount: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        return true;
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

    public void updateAccount(String username, String password) {
        // TODO update account
    }

    public void updateAccountLogout(UserInfo userInfo) {
        String query = "UPDATE account SET last_time_logout = ? WHERE id = ?;";

        try (Connection con = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_DYNAMIC)) {
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
            e.printStackTrace();
            LogServer.LogException("Error updateAccountLogout: " + e.getMessage());
        }
    }

}
