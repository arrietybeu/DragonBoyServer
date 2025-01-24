package nro.server.manager.skill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import nro.model.template.skill.SkillOptionTemplate;
import nro.model.template.skill.SpeacialSkillTemplate;
import nro.server.config.ConfigDB;
import nro.repositories.DatabaseConnectionPool;
import nro.model.template.entity.SkillInfo;
import nro.server.manager.IManager;
import nro.model.skill.NClass;
import nro.model.template.skill.SkillTemplate;
import nro.server.LogServer;

public class SkillManager implements IManager {

    @Getter
    private static SkillManager instance = new SkillManager();

    @Getter
    private final List<NClass> nClasses = new ArrayList<>();

    @Getter
    private final List<SkillOptionTemplate> skillOptions = new ArrayList<>();

    @Getter
    private final List<SpeacialSkillTemplate> specialSkills = new ArrayList<>();

    @Override
    public void init() {
        this.loadSkill();
        this.loadSkillOption();
        this.loadSpecialSkill();
    }

    @Override
    public void reload() {
    }

    @Override
    public void clear() {
    }

    private void loadSkill() {
        String query = "SELECT * FROM skill_class";

        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC)) {
            assert connection != null : "Connection is null";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var nClassId = resultSet.getInt("class_id");
                    var name = resultSet.getString("name");

                    NClass nClass = new NClass();
                    nClass.setClassId(nClassId);
                    nClass.setName(name);

                    var skillTemplates = this.loadSkillTemplate(connection, nClassId);

                    nClass.setSkillTemplates(skillTemplates);
                    this.nClasses.add(nClass);
                }
                LogServer.LogInit("Skill Class initialized size: " + this.nClasses.size());
            }
        } catch (Exception e) {
            LogServer.LogException("Error loadSkill: " + e.getMessage());
        }
    }


    private List<SkillTemplate> loadSkillTemplate(Connection connection, int classId) {
        List<SkillTemplate> skillTemplates = new ArrayList<>();

        String query = "SELECT * FROM skill_template WHERE class_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, classId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    SkillTemplate skillTemplate = new SkillTemplate();
                    skillTemplate.setClassId(classId);
                    skillTemplate.setId(resultSet.getByte("id"));
                    skillTemplate.setName(resultSet.getString("name"));
                    skillTemplate.setMaxPoint(resultSet.getInt("max_point"));
                    skillTemplate.setManaUseType(resultSet.getInt("mana_use_type"));
                    skillTemplate.setType(resultSet.getInt("type"));
                    skillTemplate.setIconId(resultSet.getInt("icon_id"));
                    skillTemplate.setDamInfo(resultSet.getString("dam_info"));
                    skillTemplate.setDescription(resultSet.getString("description"));
                    skillTemplates.add(skillTemplate);
                    this.loadSKillInfo(connection, skillTemplate);

                }
            }
        } catch (Exception e) {
            LogServer.LogException("Error loadSkillTemplate: " + e.getMessage());
        }
        return skillTemplates;
    }

    private void loadSKillInfo(Connection connection, SkillTemplate skillTemplate) {
        skillTemplate.getSkills().clear();
        int idSkill = skillTemplate.getId();
        String query = "SELECT * FROM skill_info WHERE skill_id = ? AND class_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, idSkill);
            preparedStatement.setInt(2, skillTemplate.getClassId());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    SkillInfo skill = new SkillInfo();
                    skill.setTemplate(skillTemplate);
                    skill.skillId = rs.getShort("id");
                    skill.point = rs.getByte("point");
                    skill.powRequire = rs.getLong("power_require");
                    skill.manaUse = rs.getInt("mana_use");
                    skill.coolDown = rs.getInt("cool_down");
                    skill.dx = rs.getInt("dx");
                    skill.dy = rs.getInt("dy");
                    skill.maxFight = rs.getInt("max_fight");
                    skill.damage = rs.getShort("damage");
                    skill.price = rs.getShort("price");
                    skill.moreInfo = rs.getString("more_info");
                    skillTemplate.addSkill(skill);
                }
            }
        } catch (Exception e) {
            LogServer.LogException("Error loadSKillInfo: " + e.getMessage());
        }
    }

    private void loadSkillOption() {
        String query = "SELECT * FROM skill_option";
        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC)) {
            assert connection != null : "Connection is null";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var id = resultSet.getInt("id");
                    var name = resultSet.getString("name");
                    SkillOptionTemplate skillOption = new SkillOptionTemplate(id, name);
                    this.skillOptions.add(skillOption);
                }
            }
        } catch (Exception e) {
            LogServer.LogException("Error loadSkillOption: " + e.getMessage());
        } finally {
            LogServer.LogInit("SkillOption initialized size: " + this.skillOptions.size());
        }
    }

    private void loadSpecialSkill() {
        String query = "SELECT * FROM special_skill";
        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC)) {
            assert connection != null : "Connection is null";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    SpeacialSkillTemplate specialSkill = new SpeacialSkillTemplate();
                    specialSkill.setId(resultSet.getInt("id"));
                    specialSkill.setName(resultSet.getString("name"));
                    this.specialSkills.add(specialSkill);
                }
            }
        } catch (Exception e) {
            LogServer.LogException("Error loadSpecialSkill: " + e.getMessage());
        } finally {
            LogServer.LogInit("SpecialSkill initialized size: " + this.specialSkills.size());
        }
    }

}
