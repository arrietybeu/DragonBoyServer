package nro.server.data_holders.data;

import lombok.Getter;
import nro.server.data_holders.IManager;
import nro.server.data_holders.YamlDataLoader;
import nro.server.model.templates.entity.MonsterTemplate;

import java.util.List;

/**
 * @author Arriety
 */
public class MonsterData implements IManager {

    @Getter
    private List<MonsterTemplate> monsters;

    @Override
    public void init() throws Throwable {
        this.monsters = YamlDataLoader.loadList("resources/data/update_data/NR_monster.yml", MonsterTemplate.class);
    }

    @Override
    public void reload() throws Throwable {
    }

    @Override
    public void clear() throws Throwable {
    }

    private static final class SingletonHolder {
        private static final MonsterData INSTANCE = new MonsterData();
    }

    public static MonsterData getInstance() {
        return MonsterData.SingletonHolder.INSTANCE;
    }

}
