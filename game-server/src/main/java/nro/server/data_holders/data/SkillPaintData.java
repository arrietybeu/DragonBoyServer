package nro.server.data_holders.data;

import nro.server.data_holders.IManager;
import nro.server.data_holders.YamlDataLoader;
import nro.server.model.template.data.SkillPaintTemplate;

import java.util.ArrayList;
import java.util.List;

public final class SkillPaintData implements IManager {

    public List<SkillPaintTemplate> templates = new ArrayList<>();

    @Override
    public void init() throws Throwable {
        templates = YamlDataLoader.loadList("resources/data/update_data/NR_skill.yml", SkillPaintTemplate.class);
    }

    @Override
    public void reload() throws Throwable {
        clear();
        init();
    }

    @Override
    public void clear() throws Throwable {
        if (templates != null) templates.clear();
        templates = null;
    }

    private static final class SingletonHolder {
        private static final SkillPaintData INSTANCE = new SkillPaintData();
    }

    public static SkillPaintData getInstance() {
        return SkillPaintData.SingletonHolder.INSTANCE;
    }

}
