package nro.server.data_holders.data;

import lombok.Getter;
import nro.server.data_holders.IManager;
import nro.server.data_holders.YamlDataLoader;
import nro.server.model.templates.entity.NpcTemplate;

import java.util.List;

/**
 * @author Arriety
 */
public class NpcData implements IManager {

    @Getter
    private List<NpcTemplate> npcTemplates;

    @Override
    public void init() throws Throwable {
        this.npcTemplates = YamlDataLoader.loadList("resources/data/update_data/Nr_npc_template.yml", NpcTemplate.class);
    }

    @Override
    public void reload() throws Throwable {
    }

    @Override
    public void clear() throws Throwable {
    }

    private static final class SingletonHolder {
        private static final NpcData INSTANCE = new NpcData();
    }

    public static NpcData getInstance() {
        return NpcData.SingletonHolder.INSTANCE;
    }
}
