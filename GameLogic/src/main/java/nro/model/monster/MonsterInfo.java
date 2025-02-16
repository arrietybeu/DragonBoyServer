package nro.model.monster;

import lombok.Data;
import nro.model.template.MonsterTemplate;
import nro.server.manager.MonsterManager;

@Data
public class MonsterInfo {

    private int id;
    private boolean isBoss;
    private byte levelBoss;

    public String getName() {
        MonsterTemplate template = MonsterManager.getInstance().getMonsterTemplate(this.id);
        return template.name();
    }
}