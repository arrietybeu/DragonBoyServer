package nro.server.data_holders.data;

import lombok.Getter;
import nro.server.data_holders.IManager;
import nro.server.data_holders.YamlDataLoader;
import nro.server.model.templates.entity.NpcTemplate;

import java.util.List;

/**
 * @author Arriety
 */
public final class NpcData implements IManager {

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

    public NpcTemplate getTemplateById(int id) {
        return npcTemplates.stream()
                .filter(template -> template.id() == id)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("NpcTemplate not found for id: " + id));
    }

    private static final class SingletonHolder {
        private static final NpcData INSTANCE = new NpcData();
    }

    public static NpcData getInstance() {
        return NpcData.SingletonHolder.INSTANCE;
    }
}
