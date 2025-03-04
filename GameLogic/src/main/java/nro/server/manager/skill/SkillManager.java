package nro.server.manager.skill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import nro.model.template.skill.SkillOptionTemplate;
import nro.server.config.ConfigDB;
import nro.repositories.DatabaseConnectionPool;
import nro.model.template.entity.SkillInfo;
import nro.server.manager.IManager;
import nro.model.skill.NClass;
import nro.model.template.skill.SkillTemplate;
import nro.server.LogServer;

@Getter
public class SkillManager implements IManager {

    @Getter
    private static SkillManager instance = new SkillManager();
    private final List<NClass> nClasses = new ArrayList<>();
    private final List<SkillOptionTemplate> skillOptions = new ArrayList<>();

    @Override
    public void init() {
        this.loadSkill();
        this.loadSkillOption();
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
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var nClassId = resultSet.getInt("class_id");
                    var name = resultSet.getString("name");
                    NClass nClass = new NClass(nClassId, name, this.loadSkillTemplate(connection, nClassId));
                    this.nClasses.add(nClass);
                }
//                LogServer.LogInit("Skill Class initialized size: " + this.nClasses.size());
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
                    this.loadSKillInfo(connection, skillTemplate);
                    skillTemplates.add(skillTemplate);
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
        String query = "SELECT * FROM skill_info WHERE skill_template_id = ? AND class_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, idSkill);
            preparedStatement.setInt(2, skillTemplate.getClassId());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    SkillInfo skill = new SkillInfo();
                    skill.setTemplate(skillTemplate);
                    skill.setSkillId(rs.getShort("skill_id"));
                    skill.setPoint(rs.getByte("point"));
                    skill.setPowRequire(rs.getLong("power_require"));
                    skill.setManaUse(rs.getInt("mana_use"));
                    skill.setCoolDown(rs.getInt("cool_down"));
                    skill.setDx(rs.getInt("dx"));
                    skill.setDy(rs.getInt("dy"));
                    skill.setMaxFight(rs.getInt("max_fight"));
                    skill.setDamage(rs.getShort("damage"));
                    skill.setPrice(rs.getShort("price"));
                    skill.setMoreInfo(rs.getString("more_info"));
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
//            LogServer.LogInit("SkillOption initialized size: " + this.skillOptions.size());
        }
    }

    public SkillInfo getSkillInfo(short skillId, int gender, int currentLevel) {
        try {
            for (NClass nClass : this.nClasses) {
                if (nClass.classId() == gender) {
                    for (SkillTemplate skillTemplate : nClass.skillTemplates()) {
                        if (skillTemplate.getId() == skillId) {
                            SkillInfo skillInfo = skillTemplate.getSkill(skillId, currentLevel);
                            if (skillInfo.getSkillId() == skillId || skillInfo.getPoint() == currentLevel) {
                                return skillInfo;
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("skillId: " + skillId + " gender: " + gender + " currentLevel: " + currentLevel + "\nmessage: " + ex.getMessage());
            return null;
        }
        return null;
    }
}
