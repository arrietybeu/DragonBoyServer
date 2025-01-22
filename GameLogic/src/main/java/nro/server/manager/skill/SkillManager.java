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

//    private void loadAllSkills() {
//        String query = "SELECT sc.class_id, sc.name AS class_name, " +
//                "st.id AS skill_id, st.name AS skill_name, st.max_point, st.mana_use_type, st.type, st.icon_id, st.dam_info, st.description, " +
//                "si.point, si.power_require, si.mana_use, si.cool_down, si.dx, si.dy, si.max_fight, si.damage, si.price, si.more_info " +
//                "FROM skill_class sc " +
//                "LEFT JOIN skill_template st ON sc.class_id = st.class_id " +
//                "LEFT JOIN skill_info si ON st.id = si.skill_id " +
//                "ORDER BY sc.class_id, st.id";
//
//        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC);
//             PreparedStatement preparedStatement = connection.prepareStatement(query);
//             ResultSet resultSet = preparedStatement.executeQuery()) {
//
//            Map<Integer, NClass> classMap = new HashMap<>();
//
//            while (resultSet.next()) {
//                int classId = resultSet.getInt("class_id");
//                NClass nClass = classMap.computeIfAbsent(classId, id -> {
//                    NClass newClass = new NClass();
//                    newClass.setClassId(id);
//                    newClass.setName(resultSet.getString("class_name"));
//                    return newClass;
//                });
//
//                int skillId = resultSet.getInt("skill_id");
//                if (skillId > 0) {
//                    SkillTemplate skillTemplate = nClass.getSkillTemplates().stream()
//                            .filter(skill -> skill.getSkillId() == skillId)
//                            .findFirst()
//                            .orElseGet(() -> {
//                                SkillTemplate newSkill = new SkillTemplate();
//                                newSkill.setSkillId(skillId);
//                                newSkill.setClassId(classId);
//                                newSkill.setName(resultSet.getString("skill_name"));
//                                newSkill.setMaxPoint(resultSet.getInt("max_point"));
//                                newSkill.setManaUseType(resultSet.getInt("mana_use_type"));
//                                newSkill.setType(resultSet.getInt("type"));
//                                newSkill.setIconId(resultSet.getInt("icon_id"));
//                                newSkill.setDamInfo(resultSet.getString("dam_info"));
//                                newSkill.setDescription(resultSet.getString("description"));
//                                nClass.getSkillTemplates().add(newSkill);
//                                return newSkill;
//                            });
//
//                    SkillInfo skillInfo = new SkillInfo();
//                    skillInfo.skillId = skillId;
//                    skillInfo.point = resultSet.getByte("point");
//                    skillInfo.powRequire = resultSet.getLong("power_require");
//                    skillInfo.manaUse = resultSet.getInt("mana_use");
//                    skillInfo.coolDown = resultSet.getInt("cool_down");
//                    skillInfo.dx = resultSet.getInt("dx");
//                    skillInfo.dy = resultSet.getInt("dy");
//                    skillInfo.maxFight = resultSet.getInt("max_fight");
//                    skillInfo.damage = resultSet.getShort("damage");
//                    skillInfo.price = resultSet.getShort("price");
//                    skillInfo.moreInfo = resultSet.getString("more_info");
//
//                    skillTemplate.getSkillInfo().add(skillInfo);
//                }
//            }
//
//            this.skills.putAll(classMap);
//
//        } catch (Exception e) {
//            LogServer.LogException("Error loadAllSkills: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

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
