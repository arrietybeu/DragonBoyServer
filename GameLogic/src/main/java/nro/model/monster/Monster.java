package nro.model.monster;

import lombok.Data;
import nro.model.template.MonsterTemplate;
import nro.server.manager.MonsterManager;

@Data
public class Monster {

    private int id;
    private boolean isDisable;
    private boolean isDontMove;
    private boolean isFire;
    private boolean isIce;
    private boolean isWind;
    private short templateId;
    private byte sys;
    private long hp;
    private byte level;
    private long maxp;
    private short x;
    private short y;
    private byte status;
    private byte levelBoss;
    private boolean isBoss;

    public String findNameMonsterByTemplate() {
        MonsterTemplate template = MonsterManager.getInstance().getMonsterTemplate(this.id);
        return template.name();
    }
}
