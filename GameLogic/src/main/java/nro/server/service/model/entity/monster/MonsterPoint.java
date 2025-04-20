package nro.server.service.model.entity.monster;

import lombok.Data;
import nro.server.manager.MonsterManager;
import nro.server.service.model.template.MonsterTemplate;
import nro.utils.Rnd;

@Data
public class MonsterPoint {

    private final Monster monster;
    private long hp;
    private long maxHp;
    private byte level;
    private boolean isDead;
    private long maxExp;

    public MonsterPoint(Monster monster, long maxHp, byte level) {
        this.monster = monster;
        this.maxHp = maxHp;
        this.level = level;
        this.hp = maxHp;
    }

    public boolean isDead() {
        return this.hp <= 0 || this.isDead;
    }

    public void setHp(long hp) {
        if (hp < 0) {
            this.hp = 0;
        } else {
            this.hp = hp;
        }
    }

    public void subHp(long damage) {
        damage = Math.min(damage, this.hp);
        this.hp -= damage;
    }

//    public long getDamage() {
//        MonsterTemplate template = MonsterManager.getInstance().getMonsterTemplate(this.monster.getTemplateId());
//        return template.damage();
//    }

    public long getDamage() {
        return switch (this.monster.getTemplateId()) {
            case 1, 2, 3 -> Rnd.nextInt(8, 15);
            case 4, 5, 6 -> Rnd.nextInt(15, 20);
            case 7, 8, 9 -> Rnd.nextInt(20, 35);
            case 10, 11, 12 -> Rnd.nextInt(50, 76);
            case 13, 14, 15 -> Rnd.nextInt(250, 400);
            case 16, 17, 18, 28, 29, 30 -> Rnd.nextInt(80, 150);
            case 19, 20, 21 -> Rnd.nextInt(400, 1000);
            case 22, 23, 24 -> Rnd.nextInt(450, 700);
            case 25, 26, 27 -> Rnd.nextInt(2000, 3500);
            case 31, 32, 33 -> Rnd.nextInt(250, 450);
            //
            case 39 -> Rnd.nextInt(2200, 3500);
            case 40 -> Rnd.nextInt(3200, 5000);
            case 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65 ->
                    Rnd.nextInt(5000, 15000);
            case 66, 67, 68, 69 -> Rnd.nextInt(20000, 50000);
            case 78, 79 -> Rnd.nextInt(30000, 50000);
            case 86, 87 -> Rnd.nextInt(50000, 75000);
//            default -> this.maxHp / (this.zone.map.isMapRoadSnake() ? 300 : 100);
            default -> this.maxHp / 100;
        };
    }

}