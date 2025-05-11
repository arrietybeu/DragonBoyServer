package test;

import nro.server.data_holders.YamlDataLoader;
import nro.server.model.template.data.EffectCharPaintTemplate;

public class LoadingData {

    public static void main(String[] args) throws Exception {
        var list = YamlDataLoader.loadList("resources/data/update_data/NR_effect.yml", EffectCharPaintTemplate.class);
        list.forEach(System.out::println);
    }
}
