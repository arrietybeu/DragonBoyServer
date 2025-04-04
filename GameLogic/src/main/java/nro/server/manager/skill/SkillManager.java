package nro.server.manager.skill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import nro.server.service.model.template.skill.NClassTemplate;
import nro.server.service.model.template.skill.SkillOptionTemplate;
import nro.server.config.ConfigDB;
import nro.server.service.repositories.DatabaseFactory;
import nro.server.service.model.template.entity.SkillInfo;
import nro.server.manager.IManager;
import nro.server.service.model.template.skill.SkillTemplate;
import nro.server.system.LogServer;

@Getter
public final class SkillManager implements IManager {

    @Getter
    private static SkillManager instance = new SkillManager();
    private final List<NClassTemplate> nClassTemplates = new ArrayList<>();
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
        try (Connection connection = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_STATIC)) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var nClassId = resultSet.getInt("class_id");
                    var name = resultSet.getString("name");
                    NClassTemplate nClassTemplate = new NClassTemplate(nClassId, name, this.loadSkillTemplate(connection, nClassId));
                    this.nClassTemplates.add(nClassTemplate);
                }
//                LogServer.LogInit("Skills Class initialized size: " + this.nClassTemplates.size());
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
        try (Connection connection = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_STATIC)) {
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

    public SkillInfo getSkillInfoByTemplateId(short skillId, int gender, int currentLevel) {
        try {
            for (NClassTemplate nClassTemplate : this.nClassTemplates) {
                if (nClassTemplate.classId() == gender) {
                    for (SkillTemplate skillTemplate : nClassTemplate.skillTemplates()) {
                        if (skillTemplate.getId() == skillId) {
                            SkillInfo skillInfo = skillTemplate.getSkillByTemplateId(skillId, currentLevel);
                            if (skillInfo.getPoint() == currentLevel) {
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

    public SkillInfo getSkillInfoById(int skillId, int gender, int currentLevel) {
        try {
            for (NClassTemplate nClassTemplate : this.nClassTemplates) {
                if (nClassTemplate.classId() == gender) {
                    for (SkillTemplate skillTemplate : nClassTemplate.skillTemplates()) {
                        SkillInfo skillInfo = skillTemplate.getSkillById(skillId);
                        if (skillInfo == null) continue;
                        if (skillInfo.getSkillId() == skillId || skillInfo.getPoint() == currentLevel) {
                            return skillInfo;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("skillId: " + skillId + " gender: " + gender + "\nmessage: " + ex.getMessage(), ex);
            return null;
        }
        return null;
    }
}
