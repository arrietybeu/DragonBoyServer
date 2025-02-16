package nro.model.monster;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nro.model.LiveObject;
import nro.model.template.MonsterTemplate;
import nro.server.manager.MonsterManager;

@EqualsAndHashCode(callSuper = true)
@Data
public class Monster extends LiveObject {

    private final MonsterStats stats;
    private final MonsterStatus status;
    private final MonsterInfo info;

    public Monster(int id, long maxHp, byte level, short x, short y) {
        this.setId(id);
        this.setX(x);
        this.setY(y);
        this.stats = new MonsterStats(maxHp, level);
        this.info = new MonsterInfo();
        this.status = new MonsterStatus();
    }

    public String findNameMonsterByTemplate() {
        MonsterTemplate template = MonsterManager.getInstance().getMonsterTemplate(this.getId());
        return template.name();
    }

    @Override
    public void update() {
    }
}
