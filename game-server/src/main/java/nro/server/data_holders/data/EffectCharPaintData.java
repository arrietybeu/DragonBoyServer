package nro.server.data_holders.data;

import nro.server.data_holders.GameEngine;
import nro.server.data_holders.YamlDataLoader;
import nro.server.model.templates.data.EffectCharPaintTemplate;

import java.util.List;

public final class EffectCharPaintData implements GameEngine {

    public List<EffectCharPaintTemplate> effectCharPaintTemplates;

    @Override
    public void init() throws Throwable {
        this.effectCharPaintTemplates = YamlDataLoader.loadList("resources/data/update_data/NR_effect.yml", EffectCharPaintTemplate.class);
    }

    @Override
    public void reload() throws Throwable {
        clear();
        init();
    }

    @Override
    public void clear() throws Throwable {
        if (effectCharPaintTemplates != null) effectCharPaintTemplates.clear();
        effectCharPaintTemplates = null;
    }

    private static final class SingletonHolder {
        private static final EffectCharPaintData INSTANCE = new EffectCharPaintData();
    }

    public static EffectCharPaintData getInstance() {
        return EffectCharPaintData.SingletonHolder.INSTANCE;
    }

}
