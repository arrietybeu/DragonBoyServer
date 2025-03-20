package nro.service.model.model.monster;

import lombok.Data;
import nro.service.model.model.template.MonsterTemplate;
import nro.server.manager.MonsterManager;

@Data
public class MonsterInfo {

    private final Monster monster;

    private boolean isBoss;
    private byte levelBoss;
    private long lastTimeDie;
    private long lastTimeAttack;

    public MonsterInfo(Monster monster) {
        this.monster = monster;
    }

    public String getName() {
        MonsterTemplate template = MonsterManager.getInstance().getMonsterTemplate(this.monster.getTemplateId());
        return template.name();
    }

    public int getType() {
        MonsterTemplate template = MonsterManager.getInstance().getMonsterTemplate(this.monster.getTemplateId());
        return template.type();
    }
}