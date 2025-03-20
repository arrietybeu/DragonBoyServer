package nro.server.manager;


import lombok.Getter;
import nro.server.config.ConfigDB;
import nro.service.repositories.DatabaseConnectionPool;
import nro.service.model.template.MonsterTemplate;
import nro.server.LogServer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Arriety
 */
public class MonsterManager implements IManager {

    @Getter
    private static MonsterManager instance = new MonsterManager();

    private final List<MonsterTemplate> MONSTERS = new ArrayList<>();

    @Override
    public void init() {
        this.clear();
        this.loadMonsterTemplates();
    }

    @Override
    public void reload() {
    }

    @Override
    public void clear() {
        this.MONSTERS.clear();
    }

    private void loadMonsterTemplates() {
        String query = "SELECT * FROM monster_template";
        try (Connection conn = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC);
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {

                var id = rs.getInt("id");
                var type = rs.getByte("type");
                var name = rs.getString("name");
                var hp = rs.getLong("hp");
                var rangeMove = rs.getByte("range_move");
                var speed = rs.getByte("speed");
                var dartType = rs.getByte("dart_type");

                MonsterTemplate template = new MonsterTemplate(id, type, name, hp, rangeMove, speed, dartType);
                this.MONSTERS.add(template);
            }
//            LogServer.LogInit("MonsterManager initialized size: " + MONSTERS.size());
        } catch (Exception e) {
            LogServer.LogException("Error loadMonsterTemplates: " + e.getMessage(), e);
        }
    }

    public MonsterTemplate getMonsterTemplate(int id) {
        return this.MONSTERS.get(id);
    }

    public List<MonsterTemplate> getMonsterTemplates() {
        return MONSTERS;
    }

    public int sizeMonster() {
        return MONSTERS.size();
    }

}
