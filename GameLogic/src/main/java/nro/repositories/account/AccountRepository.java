package nro.repositories.account;

import lombok.Getter;
import nro.server.config.ConfigDB;
import nro.repositories.DatabaseConnectionPool;
import nro.model.template.entity.UserInfo;
import nro.server.manager.UserManager;
import nro.service.Service;
import nro.server.manager.SessionManager;
import nro.server.LogServer;
import nro.utils.Util;

import java.sql.*;

@SuppressWarnings("ALL")
public class AccountRepository {

    @Getter
    private static final AccountRepository instance = new AccountRepository();

    public boolean checkAccount(UserInfo userInfo) {
        try {
            String username = userInfo.getUsername();
            String password = userInfo.getPassword();

            if (username.isEmpty() || password.isEmpty() || username.equals("1") || password.equals("1")) {
                Service.dialogMessage(userInfo.getSession(), "Vui lòng nhập thông tin đăng nhập hợp lệ");
                return false;
            }

            String query = "SELECT * FROM `account` WHERE `username` = ? AND `password` = ? LIMIT 1;";

            try (Connection conn = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_DYNAMIC, "login"); PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, username);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        Service.dialogMessage(userInfo.getSession(), "Thông tin đăng nhập không chính xác");
                        userInfo.getSession().getSessionInfo().constLogin++;
                        if (userInfo.getSession().getSessionInfo().constLogin > 10) {
                            userInfo.getSession().getSessionInfo().setBanUntil(System.currentTimeMillis() + 3 * 60 * 1000);
                            Service.dialogMessage(userInfo.getSession(), "Bạn đã đăng nhập sai quá nhiều lần. Vui lòng đợi 3 phút để thử lại.");
                        }
                        Util.delay(2000);
                        SessionManager.getInstance().kickSession(userInfo.getSession());
                        return false;
                    } else {
                        userInfo.setId(rs.getInt("id"));
                        UserInfo plOnline = UserManager.getInstance().get(userInfo.getId());
                        if (plOnline != null) {
                            System.out.println("plOnline: " + plOnline.getUsername());
                            String text = "Tài khoản đã có người đăng nhập xin quay lại sau vài phút";
                            Service.dialogMessage(plOnline.getSession(), text);
                            Service.dialogMessage(userInfo.getSession(), text);
                            Util.delay(2000);
                            SessionManager.getInstance().kickSession(userInfo.getSession());
                            SessionManager.getInstance().kickSession(plOnline.getSession());
                            return false;
                        }

                        userInfo.setAdmin(rs.getBoolean("is_admin"));
                        userInfo.setActive(rs.getBoolean("active"));
                        userInfo.setBan(rs.getBoolean("ban"));

                        long lastTimeLogin = rs.getTimestamp("last_time_login").getTime();
                        long lastTimeLogout = rs.getTimestamp("last_time_logout").getTime();
                        long currentTime = System.currentTimeMillis();

                        userInfo.setLastTimeLogin(lastTimeLogin);
                        userInfo.setLastTimeLogout(lastTimeLogout);

                        if ((lastTimeLogin - lastTimeLogout) <= 5000 && (currentTime - lastTimeLogout) < 10000) {
                            long waitTime = 10000 - (currentTime - lastTimeLogout);
                            short time = (short) (waitTime / 1000);

                            Service.sendLoginDe(userInfo.getSession(), time);
                            System.out.println("waitTime: " + waitTime);
                            try {
                                Thread.sleep(time * 1000L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            UserInfo pl = UserManager.getInstance().get(userInfo.getId());
                            if (pl != null) {
                                System.out.println("plOnline: " + pl.getUsername());
                                String text = "Tài khoản đã có người đăng nhập xin quay lại sau vài phút";
                                Service.dialogMessage(pl.getSession(), text);
                                Service.dialogMessage(userInfo.getSession(), text);
                                Util.delay(2000);
                                SessionManager.getInstance().kickSession(userInfo.getSession());
                                SessionManager.getInstance().kickSession(pl.getSession());
                                return false;
                            }

                            System.out.println("vao game");
                        }

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

    public static void updateAccountLogout(UserInfo userInfo) {
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
