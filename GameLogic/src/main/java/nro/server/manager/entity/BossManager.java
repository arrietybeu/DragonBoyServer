package nro.server.manager.entity;

import lombok.Getter;
import nro.consts.ConstBoss;
import nro.server.config.ConfigDB;
import nro.server.config.ConfigServer;
import nro.server.manager.IManager;
import nro.server.manager.MapManager;
import nro.server.manager.skill.SkillManager;
import nro.server.realtime.system.boss.BossAISystem;
import nro.server.service.core.map.AreaService;
import nro.server.service.model.entity.ai.boss.*;
import nro.server.service.model.map.areas.Area;
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
import java.util.Objects;

public final class BossManager implements IManager {

    @Getter
    private static final BossManager instance = new BossManager();
    private final Map<Integer, Boss> bossTemplates = new HashMap<>();

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
        BossAISystem.getInstance().removeAll();
        bossTemplates.clear();
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
                byte spawnType = rs.getByte("spawn_type");
                boolean isAutoSpawn = rs.getByte("auto_despawn") == 1;
                byte typeLeave = rs.getByte("type_leaving_map");

                JSONArray mapsIdArray = (JSONArray) JSONValue.parse(mapsId);

                BossFashion fashion = loadBossFashion(rs);
                Boss boss = BossFactory.getInstance().createBoss(bossId, fashion);
                if (boss != null) {
                    boss.setId(bossId);
                    boss.setName(name);
                    boss.setGender(gender);
                    boss.setX(x);
                    boss.setY(y);
                    boss.setRespawnTime(respawnTime);
                    boss.setAfkTimeout(afkTimeOut);
                    boss.setMapsId(new int[mapsIdArray.size()]);
                    boss.setSpawnType(spawnType);
                    boss.setAutoDespawn(isAutoSpawn);
                    boss.setTypeLeaveMap(typeLeave);
                    for (int i = 0; i < mapsIdArray.size(); i++) {
                        boss.getMapsId()[i] = Short.parseShort(mapsIdArray.get(i).toString());
                    }

                    BossPoints points = loadBossPoints(boss, rs);
                    BossSkill skills = loadBossSkills(boss);
                    boss.setPoints(points);
                    boss.setSkills(skills);
                    bossTemplates.put(bossId, boss);

                    if (spawnType == ConstBoss.BOSS_SPAWN_TYPE_NORMAL) {
                        Area defaultArea = Objects.requireNonNull(MapManager.getInstance().findMapById(boss.getMapsId()[0])).getArea(-1, boss);
                        Boss activeBoss = BossFactory.getInstance().createBossFromTemplate(bossId);
                        if (activeBoss != null) {
                            activeBoss.setX(boss.getX());
                            activeBoss.setY(boss.getY());
                            activeBoss.setArea(defaultArea);
                            AreaService.getInstance().changerMapByShip(activeBoss, defaultArea.getMap().getId(), activeBoss.getX(), activeBoss.getY(), 1, defaultArea);
                            BossAISystem.getInstance().register(activeBoss);
                        } else {
                            LogServer.LogException("BossManager.loadBoss() error: Boss " + boss.getName() + " spawn failed!");
                        }
                    }
                    LogServer.LogInit("Loaded Boss: " + boss.getName() + " with ID: " + boss.getId() + " spawn Type: " + spawnType);
                }
            }
        } catch (Exception e) {
            LogServer.LogException("BossManager.init() error: " + e.getMessage(), e);
        }
    }

    private BossPoints loadBossPoints(Boss boss, ResultSet rs) throws SQLException {
        BossPoints points = new BossPoints(boss);
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

    private BossSkill loadBossSkills(Boss boss) throws SQLException {
        BossSkill bossSkill = new BossSkill(boss);
        try (Connection conn = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_ENTITY);
             PreparedStatement ps = conn.prepareStatement(BOSS_SKILL_QUERY)) {

            ps.setInt(1, boss.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int skillId = rs.getInt("skill_id");
                    int level = rs.getInt("skill_level");

                    // Gọi từ hệ thống template của bạn
                    SkillInfo skillInfo = SkillManager.getInstance().getSkillInfoByTemplateId((short) skillId, boss.getGender(), level);
                    if (skillInfo != null) {
                        bossSkill.addSkill(skillInfo);
                    }
                }
            }
        }
        return bossSkill;
    }

    public Boss getTemplateById(int bossId) {
        return bossTemplates.get(bossId);
    }

    public int size() {
        return bossTemplates.size();
    }
}
