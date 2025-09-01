package nro.server.data_holders.data;

import lombok.Getter;
import nro.commons.database.Database;
import nro.commons.utils.NetworkUtils;
import nro.server.configs.main.ConfigServer;
import nro.server.data_holders.GameEngine;
import nro.server.model.templates.skill.NClassTemplate;
import nro.server.model.templates.skill.SkillInfo;
import nro.server.model.templates.skill.SkillTemplate;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arriety
 */
public final class SkillData implements GameEngine {

    @Getter
    private final List<NClassTemplate> nClassTemplates = new ArrayList<>();

    public byte[] skillData;

    @Override
    public void init() throws Throwable {
        loadSkill();
        setSkillData();
    }

    @Override
    public void reload() throws Throwable {
    }

    @Override
    public void clear() throws Throwable {
    }

    private void loadSkill() {
        String query = "SELECT * FROM skill_class";

        Database.withConnection(connection -> {
            Database.select(connection, query, resultSet -> {
                while (resultSet.next()) {
                    var nClassId = resultSet.getInt("class_id");
                    var name = resultSet.getString("name");
                    NClassTemplate nClassTemplate = new NClassTemplate(nClassId, name, this.loadSkillTemplate(connection, nClassId));
                    this.nClassTemplates.add(nClassTemplate);
                }
            });
            return null;
        });
    }

    private List<SkillTemplate> loadSkillTemplate(Connection con, int classId) {
        List<SkillTemplate> skillTemplates = new ArrayList<>();

        String query = "SELECT * FROM skill_template WHERE class_id = ?";
        Database.select(con, query, resultSet -> {
            while (resultSet.next()) {
                SkillTemplate skillTemplate = new SkillTemplate();
                skillTemplate.setClassId(classId);
                skillTemplate.setId(resultSet.getByte("id"));
                skillTemplate.setName(resultSet.getString("name"));
                skillTemplate.setMaxPoint(resultSet.getByte("max_point"));
                skillTemplate.setManaUseType(resultSet.getByte("mana_use_type"));
                skillTemplate.setType(resultSet.getByte("type"));
                skillTemplate.setIconId(resultSet.getShort("icon_id"));
                skillTemplate.setDamInfo(resultSet.getString("dam_info"));
                skillTemplate.setDescription(resultSet.getString("description"));
                loadSKillInfo(con, skillTemplate);
                skillTemplates.add(skillTemplate);
            }
        }, preparedStatement -> preparedStatement.setInt(1, classId));
        return skillTemplates;
    }

    private void loadSKillInfo(Connection connection, SkillTemplate skillTemplate) {
        skillTemplate.getSkills().clear();
        int idSkill = skillTemplate.getId();

        String query = "SELECT * FROM skill_info WHERE skill_template_id = ? AND class_id = ?";
        Database.select(connection, query, rs -> {
            while (rs.next()) {
                SkillInfo skill = new SkillInfo();
                skill.setTemplate(skillTemplate);
                skill.setSkillId(rs.getShort("skill_id"));
                skill.setPoint(rs.getByte("point"));
                skill.setPowRequire(rs.getLong("power_require"));
                skill.setManaUse(rs.getShort("mana_use"));
                skill.setBaseCooldown(rs.getInt("cool_down"));
                skill.setDx(rs.getShort("dx"));
                skill.setDy(rs.getShort("dy"));
                skill.setMaxFight(rs.getByte("max_fight"));
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

    private void setSkillData() {
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);

        var nClasses = this.getNClassTemplates();
        buffer.put((byte) ConfigServer.VERSION_DATA_SKILL);
        buffer.put((byte) 0);
        buffer.put((byte) nClasses.size());
        for (var classSkill : nClasses) {
            NetworkUtils.writeString(buffer, classSkill.name());
            buffer.put((byte) classSkill.skillTemplates().size());
            for (var skillTemplate : classSkill.skillTemplates()) {
                buffer.put(skillTemplate.getId());
                NetworkUtils.writeString(buffer, skillTemplate.getName());
                buffer.put(skillTemplate.getMaxPoint());
                buffer.put(skillTemplate.getManaUseType());
                buffer.put(skillTemplate.getType());
                buffer.putShort(skillTemplate.getIconId());
                NetworkUtils.writeString(buffer, skillTemplate.getDamInfo());
                NetworkUtils.writeString(buffer, skillTemplate.getDescription());
                buffer.put((byte) skillTemplate.getSkills().size());
                for (var skill : skillTemplate.getSkills()) {
                    buffer.putShort(skill.getSkillId());
                    buffer.put(skill.getPoint());
                    buffer.putLong(skill.getPowRequire());
                    buffer.putShort(skill.getManaUse());
                    buffer.putInt(skill.getBaseCooldown());
                    buffer.putShort(skill.getDx());
                    buffer.putShort(skill.getDy());
                    buffer.put(skill.getMaxFight());
                    buffer.putShort(skill.getDamage());
                    buffer.putShort(skill.getPrice());
                    NetworkUtils.writeString(buffer, skill.getMoreInfo());
                }
            }
        }
        buffer.flip();
        this.skillData = new byte[buffer.limit()];
        buffer.get(this.skillData);
    }

    public static SkillData getInstance() {
        return SkillDataHolder.INSTANCE;
    }

    private static class SkillDataHolder {
        private static final SkillData INSTANCE = new SkillData();
    }

}
