package nro.server.data_holders.data;

import lombok.Getter;
import nro.commons.database.Database;
import nro.server.data_holders.IManager;
import nro.server.model.templates.skill.NClassTemplate;
import nro.server.model.templates.skill.SkillInfo;
import nro.server.model.templates.skill.SkillTemplate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arriety
 */
public final class SkillData implements IManager {

    @Getter
    private final List<NClassTemplate> nClassTemplates = new ArrayList<>();

    @Override
    public void init() throws Throwable {
        loadSkill();
    }

    @Override
    public void reload() throws Throwable {
    }

    @Override
    public void clear() throws Throwable {
    }

    private void loadSkill() {
        String query = "SELECT * FROM skill_class";
        Database.select(query, resultSet -> {
            while (resultSet.next()) {
                var nClassId = resultSet.getInt("class_id");
                var name = resultSet.getString("name");
                NClassTemplate nClassTemplate = new NClassTemplate(nClassId, name, this.loadSkillTemplate(nClassId));
                this.nClassTemplates.add(nClassTemplate);
            }
        });
    }

    private List<SkillTemplate> loadSkillTemplate(int classId) throws SQLException {
        List<SkillTemplate> skillTemplates = new ArrayList<>();

        String query = "SELECT * FROM skill_template WHERE class_id = ?";
        Database.select(query, resultSet -> {
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
                loadSKillInfo(skillTemplate);
                skillTemplates.add(skillTemplate);
            }
        }, preparedStatement -> preparedStatement.setInt(1, classId));
        return skillTemplates;
    }

    private void loadSKillInfo(SkillTemplate skillTemplate) {
        skillTemplate.getSkills().clear();
        int idSkill = skillTemplate.getId();

        String query = "SELECT * FROM skill_info WHERE skill_template_id = ? AND class_id = ?";
        Database.select(query, rs -> {
            while (rs.next()) {
                SkillInfo skill = new SkillInfo();
                skill.setTemplate(skillTemplate);
                skill.setSkillId(rs.getShort("skill_id"));
                skill.setPoint(rs.getByte("point"));
                skill.setPowRequire(rs.getLong("power_require"));
                skill.setManaUse(rs.getInt("mana_use"));
                skill.setBaseCooldown(rs.getInt("cool_down"));
                skill.setDx(rs.getInt("dx"));
                skill.setDy(rs.getInt("dy"));
                skill.setMaxFight(rs.getInt("max_fight"));
                skill.setDamage(rs.getShort("damage"));
                skill.setPrice(rs.getShort("price"));
                skill.setMoreInfo(rs.getString("more_info"));
                skillTemplate.addSkill(skill);
            }
        }, stmt -> {
            stmt.setInt(1, idSkill);
            stmt.setInt(2, skillTemplate.getClassId());
        });
    }

    public static SkillData getInstance() {
        return SkillDataHolder.INSTANCE;
    }

    private static class SkillDataHolder {
        private static final SkillData INSTANCE = new SkillData();
    }

}
