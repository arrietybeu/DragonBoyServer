package nro.server.manager.skill;

import lombok.Getter;
import nro.consts.ConstPlayer;
import nro.service.model.template.skill.SpeacialSkillTemplate;
import nro.service.repositories.DatabaseConnectionPool;
import nro.server.LogServer;
import nro.server.config.ConfigDB;
import nro.server.manager.IManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Getter
public class SpeacialSkillManager implements IManager {

    @Getter
    private static SpeacialSkillManager instance = new SpeacialSkillManager();

    private final List<SpeacialSkillTemplate> specialSkills = new ArrayList<>();
    private final List<SpeacialSkillTemplate> traiDat = new ArrayList<>();
    private final List<SpeacialSkillTemplate> xayda = new ArrayList<>();
    private final List<SpeacialSkillTemplate> namec = new ArrayList<>();

    @Override
    public void init() {
        this.loadSpecialSkill();
    }

    @Override
    public void reload() {
        this.clear();
        this.loadSpecialSkill();
    }

    @Override
    public void clear() {
        this.specialSkills.clear();
        this.traiDat.clear();
        this.xayda.clear();
        this.namec.clear();
    }

    private void loadSpecialSkill() {
        String query = "SELECT * FROM skill_special";
        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC)) {
            assert connection != null : "Connection is null";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    SpeacialSkillTemplate specialSkill = new SpeacialSkillTemplate();
                    specialSkill.setId(resultSet.getInt("id"));
                    specialSkill.setName(resultSet.getString("name"));
                    specialSkill.setIcon(resultSet.getShort("icon"));
                    specialSkill.setGender(resultSet.getByte("gender"));
                    specialSkill.setParamFrom1(resultSet.getInt("param_from_1"));
                    specialSkill.setParamTo1(resultSet.getInt("param_to_1"));
                    specialSkill.setParamFrom2(resultSet.getInt("param_from_2"));
                    specialSkill.setParamTo2(resultSet.getInt("param_to_2"));

                    switch (specialSkill.getGender()) {
                        case ConstPlayer.TRAI_DAT -> this.traiDat.add(specialSkill);
                        case ConstPlayer.NAMEC -> this.namec.add(specialSkill);
                        case ConstPlayer.XAYDA -> this.xayda.add(specialSkill);
                        default -> {
                            this.traiDat.add(specialSkill);
                            this.namec.add(specialSkill);
                            this.xayda.add(specialSkill);
                        }
                    }
                    this.specialSkills.add(specialSkill);
                }
            }
//            LogServer.LogInit("SpecialSkill initialized size: " + this.specialSkills.size());

        } catch (Exception e) {
            LogServer.LogException("Error loadSpecialSkill: " + e.getMessage());
        }
    }
}
