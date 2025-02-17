package nro.server.manager;

import lombok.Data;
import lombok.Getter;
import nro.model.player.Player;
import nro.model.template.CaptionTemplate;
import nro.network.Message;
import nro.repositories.DatabaseConnectionPool;
import nro.server.LogServer;
import nro.server.config.ConfigDB;

import java.io.DataOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public class CaptionManager implements IManager {

    @Getter
    private static CaptionManager instance = new CaptionManager();

    private final List<CaptionTemplate> CAPTIONS = new ArrayList<>();
    private final List<CaptionTemplate.CaptionLevel> CAPTION_LEVELS = new ArrayList<>();

    private byte[] traiDat;
    private byte[] namec;
    private byte[] xayda;

    @Override
    public void init() {
        this.loadCaption();
        this.loadCaptionLevel();
        this.setDataCaptionLevel();
    }

    @Override
    public void reload() {
        clear();
        init();
    }

    @Override
    public void clear() {
        CAPTIONS.clear();
        CAPTION_LEVELS.clear();
    }

    private void loadCaption() {
        String query = "SELECT * FROM game_caption";
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

    private void loadCaptionLevel() {
        String query = "SELECT * FROM game_caption_level";
        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC);
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             var rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                int id = rs.getShort("id");
                byte gender = rs.getByte("gender");
                String name = rs.getString("name");

                var captionLevel = new CaptionTemplate.CaptionLevel(id, gender, name);
                CAPTION_LEVELS.add(captionLevel);
            }
            LogServer.LogInit("Loaded " + CAPTION_LEVELS.size() + " caption levels");

        } catch (Exception e) {
            LogServer.LogException("Error loading caption level: " + e.getMessage());
        }
    }

    public List<CaptionTemplate.CaptionLevel> getCaptionLevelsByGender(byte gender) {
        List<CaptionTemplate.CaptionLevel> result = new ArrayList<>();
        for (CaptionTemplate.CaptionLevel cl : CAPTION_LEVELS) {
            if (cl.gender() == gender) {
                result.add(cl);
            }
        }
        return result;
    }

    public void setDataCaptionLevel() {
        try {
            for (int i = 0; i <= 2; i++) {
                List<CaptionTemplate.CaptionLevel> captionLevels = this.getCaptionLevelsByGender((byte) i);
                try (Message message = new Message()) {
                    DataOutputStream data = message.writer();
                    data.writeByte(captionLevels.size());
                    for (var caption : captionLevels) {
                        data.writeUTF(caption.name());
                    }

                    byte[] captionData = message.getData();
                    switch (i) {
                        case 0 -> traiDat = captionData;
                        case 1 -> namec = captionData;
                        case 2 -> xayda = captionData;
                    }

                } catch (Exception ex) {
                    LogServer.LogException("Error setting Data Caption Level for gender " + i + ": " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("Unexpected error in setDataCaptionLevel: " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    public int getLevel(Player player) {
        long power = player.getPlayerStats().getPower();
        return CAPTIONS.stream()
                .sorted(Comparator.comparingLong(CaptionTemplate::getExp).reversed())
                .filter(caption -> power >= caption.getExp())
                .findFirst()
                .map(CAPTIONS::indexOf)
                .orElse(0);
    }

}
