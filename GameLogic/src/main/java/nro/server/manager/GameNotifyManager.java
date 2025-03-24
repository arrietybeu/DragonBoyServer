package nro.server.manager;

import lombok.Getter;
import nro.service.model.template.GameInfo;
import nro.server.network.Message;
import nro.service.repositories.DatabaseConnectionPool;
import nro.server.system.LogServer;
import nro.server.config.ConfigDB;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GameNotifyManager implements IManager {

    @Getter
    private static final GameNotifyManager instance = new GameNotifyManager();

    private final List<GameInfo> notifyList = new ArrayList<>();
    private byte[] dataNotify;

    @Override
    public void init() {
        this.loadNotify();
        this.setDataNotify();
    }

    @Override
    public void reload() {
        this.clear();
        this.init();
    }

    @Override
    public void clear() {
        this.notifyList.clear();
        this.dataNotify = null;
    }

    private void loadNotify() {
        String sql = "SELECT * FROM `game_notify`";
        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC)) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var id = resultSet.getShort("id");
                    var textMain = resultSet.getString("main");
                    var content = resultSet.getString("content");

                    GameInfo gameInfo = new GameInfo(id, textMain, content);
                    this.notifyList.add(gameInfo);
                }
//                LogServer.LogInit("GameInfo initialized size: " + this.notifyList.size());
            }
        } catch (SQLException ex) {
            LogServer.LogException("Error loadNotify: " + ex.getMessage());
        }
    }

    private void setDataNotify() {
        try (Message message = new Message()) {
            DataOutputStream data = message.writer();

            data.writeByte(this.notifyList.size());
            for (var gameInfo : this.notifyList) {
                data.writeShort(gameInfo.id());
                data.writeUTF(gameInfo.main());
                data.writeUTF(gameInfo.content());
            }

            this.dataNotify = message.getData();

        } catch (IOException ex) {
            LogServer.LogException("Error set data Notify: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
