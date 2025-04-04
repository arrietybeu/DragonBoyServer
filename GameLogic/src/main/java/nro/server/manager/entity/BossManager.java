package nro.server.manager.entity;

import lombok.Getter;
import nro.server.config.ConfigDB;
import nro.server.config.ConfigServer;
import nro.server.manager.IManager;
import nro.server.manager.skill.SkillManager;
import nro.server.realtime.system.boss.BossAISystem;
import nro.server.service.model.entity.ai.boss.*;
import nro.server.service.model.template.entity.SkillInfo;
import nro.server.service.repositories.DatabaseFactory;
import nro.server.system.LogServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class BossManager implements IManager {

    @Getter
    private static final BossManager instance = new BossManager();
    private final Map<Integer, Boss> bosses = new HashMap<>();

    private static final String BOSS_DATA_QUERY = "SELECT * FROM boss_data";
    private static final String BOSS_SKILL_QUERY = "SELECT * FROM boss_skills WHERE boss_id = ?";

    @Override
    public void init() {
        try {
            BossFactory.getInstance().init(ConfigServer.PATH_BOSS_HANDLER);
            loadBoss();
        } catch (Exception e) {
            LogServer.LogException("BossManager.init() error: " + e.getMessage(), e);
        }
    }

    @Override
    public void reload() {
        this.clear();
        this.init();
    }

    @Override
    public void clear() {
        // TODO clear all boss data
    }

    private void loadBoss() {
        try (Connection conn = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_ENTITY); PreparedStatement ps = conn.prepareStatement(BOSS_DATA_QUERY); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int bossId = rs.getInt("id");
                String name = rs.getString("name");
                byte gender = rs.getByte("gender");
                String mapsId = rs.getString("maps_id");
                int respawnTime = rs.getInt("respawn_time");
                int afkTimeOut = rs.getInt("afk_time_out");
                short x = rs.getShort("x");
                short y = rs.getShort("y");

                JSONArray mapsIdArray = (JSONArray) JSONValue.parse(mapsId);

                BossPoints points = loadBossPoints(rs);
                BossFashion fashion = loadBossFashion(rs);
                BossSkill skills = loadBossSkills(bossId, gender);

                Boss boss = BossFactory.getInstance().createBoss(bossId, points, fashion, skills);
                if (boss != null) {
                    boss.setId(bossId);
                    boss.setName(name);
                    boss.setGender(gender);
                    boss.setX(x);
                    boss.setY(y);
                    boss.setRespawnTime(respawnTime);
                    boss.setAfkTimeout(afkTimeOut);
                    boss.setMapsId(new int[mapsIdArray.size()]);
                    for (int i = 0; i < mapsIdArray.size(); i++) {
                        boss.getMapsId()[i] = Short.parseShort(mapsIdArray.get(i).toString());
                    }
                    BossAISystem.getInstance().register(boss);
                    bosses.put(bossId, boss);
                    LogServer.LogInit("Loaded Boss: " + boss.getName());
                }
            }

        } catch (Exception e) {
            LogServer.LogException("BossManager.init() error: " + e.getMessage(), e);
        }
    }

    private BossPoints loadBossPoints(ResultSet rs) throws SQLException {
        BossPoints points = new BossPoints();
        points.setBaseHP(rs.getInt("base_hp"));
        points.setBaseMP(rs.getInt("base_mp"));
        points.setBaseDamage(rs.getInt("base_damage"));
        points.setBaseDefense(rs.getInt("base_defense"));
        points.setMovementSpeed(rs.getByte("speed"));

        points.setMaxHP(points.getBaseHP());
        points.setMaxMP(points.getBaseMP());
        points.setCurrentHp(points.getMaxHP());
        points.setCurrentMp(points.getMaxMP());

        return points;
    }

    private BossFashion loadBossFashion(ResultSet rs) throws SQLException {
        return new BossFashion(rs.getShort("head"), rs.getShort("body"), rs.getShort("leg"), rs.getShort("mount"), rs.getShort("flag_bag"), rs.getShort("aura"), rs.getByte("eff_set_item"), rs.getShort("hat_id"));
    }

    private BossSkill loadBossSkills(int bossId, int gender) throws SQLException {
        BossSkill bossSkill = new BossSkill();
        try (Connection conn = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_ENTITY); PreparedStatement ps = conn.prepareStatement(BOSS_SKILL_QUERY)) {

            ps.setInt(1, bossId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int skillId = rs.getInt("skill_id");
                    int level = rs.getInt("skill_level");

                    // Gọi từ hệ thống template của bạn
                    SkillInfo skillInfo = SkillManager.getInstance().getSkillInfoByTemplateId((short) skillId, gender, level);
                    if (skillInfo != null) {
                        bossSkill.addSkill(skillInfo);
                    }
                }
            }
        }
        return bossSkill;
    }

}
