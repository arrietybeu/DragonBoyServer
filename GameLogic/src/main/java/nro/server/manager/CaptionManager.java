package nro.server.manager;

import lombok.Getter;
import nro.model.template.CaptionTemplate;
import nro.repositories.DatabaseConnectionPool;
import nro.server.LogServer;
import nro.server.config.ConfigDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class CaptionManager implements IManager {

    @Getter
    private static CaptionManager instance = new CaptionManager();
    @Getter
    private final List<CaptionTemplate> CAPTIONS = new ArrayList<>();

    @Override
    public void init() {
        this.loadCaption();
    }

    @Override
    public void reload() {
    }

    @Override
    public void clear() {
    }

    private void loadCaption() {
        String query = "SELECT * FROM caption";
        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC);
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             var rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                int id = rs.getShort("id");
                long exp = rs.getLong("exp");
                CaptionTemplate caption = new CaptionTemplate(id, exp);
                CAPTIONS.add(caption);
            }
            LogServer.LogInit("Loaded " + CAPTIONS.size() + " captions");

        } catch (Exception e) {
            LogServer.LogException("Error loading caption: " + e.getMessage());
        }
    }
}
