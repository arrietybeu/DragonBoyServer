package nro.model.monster;

import lombok.Data;

@Data
public class MonsterStats {

    private long hp;
    private long maxHp;
    private byte level;

    public MonsterStats(long maxHp, byte level) {
        this.maxHp = maxHp;
        this.level = level;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public void takeDamage(long damage) {
        this.hp = Math.max(0, this.hp - damage);
    }

}