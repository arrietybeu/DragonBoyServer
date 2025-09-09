package nro.server.data_holders.data;

import lombok.Getter;
import nro.server.data_holders.GameEngine;
import nro.server.data_holders.YamlDataLoader;
import nro.server.model.templates.entity.MonsterTemplate;

import java.util.Map;

/**
 * @author Arriety
 */
public final class MonsterData implements GameEngine {

    @Getter
    private Map<Integer, MonsterTemplate> monsters;

    @Override
    public void init() throws Throwable {
        this.monsters = YamlDataLoader.loadMap("resources/data/update_data/NR_monster.yml", Integer.class, MonsterTemplate.class);
    }

    @Override
    public void reload() throws Throwable {
    }

    @Override
    public void clear() throws Throwable {
    }

    public MonsterTemplate getMonster(int monsterId) {
        return monsters.get(monsterId);
    }

    private static final class SingletonHolder {
        private static final MonsterData INSTANCE = new MonsterData();
    }

    public static MonsterData getInstance() {
        return MonsterData.SingletonHolder.INSTANCE;
    }

}
