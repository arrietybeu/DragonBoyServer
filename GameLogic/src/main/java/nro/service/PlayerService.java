package nro.service;

import nro.model.player.Player;
import nro.network.Session;
import nro.repositories.DatabaseConnectionPool;
import nro.repositories.player.PlayerCreator;
import nro.repositories.player.PlayerLoader;
import nro.server.LogServer;
import nro.server.config.ConfigDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class PlayerService {

    private static PlayerService instance;

    public static PlayerService getInstance() {
        if (instance == null) {
            instance = new PlayerService();
        }
        return instance;
    }

    public void finishUpdateHandler(Session session) {
        System.out.println("finishUpdateHandler");
        Player player = PlayerLoader.getInstance().loadPlayer(session);
        if (player == null) {
            Service.initSelectChar(session);
        } else {
            System.out.println("create player success for player name: " + player.getName());
            session.setPlayer(player);
            this.onPlayerLoginSuccess();
        }
    }

    public void onPlayerLoginSuccess() {
    }

    /**
     * Xử lý logic tạo nhân vật trong cơ sở dữ liệu.
     *
     * @param session Session hiện tại
     * @param name    Tên nhân vật
     * @param gender  Hành tinh nhân vật
     * @param hair    Kiểu tóc nhân vật
     * @return true nếu tạo thành công, false nếu không
     * @throws SQLException Nếu xảy ra lỗi cơ sở dữ liệu
     */

    public boolean handleCharacterCreation(Session session, String name, byte gender, byte hair) throws SQLException {
        final String QUERY_CHECK = "SELECT 1 FROM player WHERE name = ? OR account_id = ?";

        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_DYNAMIC)) {
            assert connection != null : "Connection is null";
            try (PreparedStatement psCheck = connection.prepareStatement(QUERY_CHECK)) {

                psCheck.setString(1, name);
                psCheck.setInt(2, session.getUserInfo().getId());

                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        Service.dialogMessage(session, "Tên nhân vật hoặc tài khoản đã tồn tại.");
                        return false;
                    }
                }

                System.out.println("account id: " + session.getUserInfo().getId());
                boolean isCreated = PlayerCreator.getInstance().createPlayer(
                        connection,
                        session.getUserInfo().getId(),
                        name,
                        gender,
                        hair
                );

                if (!isCreated) {
                    Service.dialogMessage(session, "Tạo nhân vật thất bại.");
                }
                return isCreated;

            }
        } catch (SQLException e) {
            LogServer.LogException(String.format(
                    "Error creating character for account_id: %d, name: %s, gender: %d, hair: %d. Error: %s",
                    session.getUserInfo().getId(), name, gender, hair, e.getMessage()
            ));
            Service.dialogMessage(session, "Đã xảy ra lỗi khi tạo nhân vật. Vui lòng thử lại, nếu vẫn không thể thao tác được vui lòng báo cáo lại Admin.");
            throw e;
        }
    }

    /**
     * Kiểm tra tính hợp lệ của dữ liệu player.
     *
     * @param name   Tên nhân vật
     * @param gender Hành Tinh nhân vật
     * @return Thông báo không hợp lệ, null nếu hợp lệ
     */

    public String validateCharacterData(String name, byte gender) {
        if (name.length() < 5 || name.length() > 10) {
            return "Tên nhân vật phải từ 5 - 10 kí tự!";
        }

        if (gender < 0 || gender > 2) {
            return "Hành tinh không hợp lệ!";
        }

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]+$");
        if (!pattern.matcher(name).matches()) {
            return "Tên nhân vật không được chứa ký tự đặc biệt, chỉ cho phép a-z, A-Z, 0-9, và _";
        }

        return null;
    }


}
