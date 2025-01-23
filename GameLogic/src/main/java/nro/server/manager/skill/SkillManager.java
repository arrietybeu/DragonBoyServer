package nro.server.manager.skill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nro.server.config.ConfigDB;
import nro.repositories.DatabaseConnectionPool;
import nro.model.template.entity.SkillInfo;
import nro.server.manager.IManager;
import nro.model.skill.NClass;
import nro.model.template.skill.SkillTemplate;
import nro.server.LogServer;

public class SkillManager implements IManager {

    private static SkillManager instance;

    private final Map<Integer, NClass> skills = new HashMap<>();

    public static SkillManager getInstance() {
        if (instance == null) {
            instance = new SkillManager();
        }
        return instance;
    }

    @Override
    public void init() {
        this.loadSkill();
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
                    NClass nClass = new NClass();

                    var nClassId = resultSet.getInt("class_id");
                    var name = resultSet.getString("name");

                    nClass.setClassId(nClassId);
                    nClass.setName(name);

                    nClass.setSkillTemplates(this.loadSkillTemplate(connection, nClassId));

                    this.skills.put(nClass.getClassId(), nClass);
                }
                LogServer.LogInit("SkillManager initialized size: " + this.getSize());
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
                    skillTemplate.setSkillId(resultSet.getByte("id"));
                    skillTemplate.setName(resultSet.getString("name"));
                    skillTemplate.setMaxPoint(resultSet.getInt("max_point"));
                    skillTemplate.setManaUseType(resultSet.getInt("mana_use_type"));
                    skillTemplate.setType(resultSet.getInt("type"));
                    skillTemplate.setIconId(resultSet.getInt("icon_id"));
                    skillTemplate.setDamInfo(resultSet.getString("dam_info"));
                    skillTemplate.setDescription(resultSet.getString("description"));
                    skillTemplate.setSkillInfo(this.loadSKillInfo(connection, skillTemplate));

                    skillTemplates.add(skillTemplate);
                }
            }
        } catch (Exception e) {
            LogServer.LogException("Error loadSkillTemplate: " + e.getMessage());
        }
        return skillTemplates;
    }

    private List<SkillInfo> loadSKillInfo(Connection connection, SkillTemplate skillTemplate) {
        List<SkillInfo> skillInfos = new ArrayList<>();

        String query = "SELECT * FROM skill_info WHERE skill_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, skillTemplate.getSkillId());

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    SkillInfo skill = new SkillInfo();
                    skill.template = skillTemplate;
                    skill.skillId = skillTemplate.getSkillId();
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
                    skillInfos.add(skill);
                }
            }
        } catch (Exception e) {
            LogServer.LogException("Error loadSKillInfo: " + e.getMessage());
        }
        return skillInfos;
    }

    public int getSize() {
        return this.skills.size();
    }

    public NClass getNClass(int classId) {
        return this.skills.get(classId);
    }

    public Map<Integer, NClass> getNClasses() {
        return this.skills;
    }

    public void logAllSkills() {
        for (var entry : skills.entrySet()) {
            int classId = entry.getKey();
            NClass nClass = entry.getValue();
            LogServer.DebugLogic("Class ID: " + classId + ", Name: " + nClass.getName());

            List<SkillTemplate> skillTemplates = nClass.getSkillTemplates();
            if (skillTemplates != null) {
                for (SkillTemplate skillTemplate : skillTemplates) {
                    LogServer.DebugLogic("  SkillTemplate ID: " + skillTemplate.getSkillId() + ", Name: " + skillTemplate.getName());

                    List<SkillInfo> skillInfos = skillTemplate.getSkillInfo();
                    if (skillInfos != null) {
                        for (SkillInfo skill : skillInfos) {
                            LogServer.DebugLogic("    SkillInfo ID: " + skill.skillId
                                    + ", Point: " + skill.point
                                    + ", PowerRequire: " + skill.powRequire
                                    + ", ManaUse: " + skill.manaUse
                                    + ", CoolDown: " + skill.coolDown
                                    + ", Damage: " + skill.damage);
                        }
                    } else {
                        LogServer.DebugLogic("    No SkillInfo available.");
                    }
                }
            } else {
                LogServer.DebugLogic("  No SkillTemplate available.");
            }
        }
    }
}
