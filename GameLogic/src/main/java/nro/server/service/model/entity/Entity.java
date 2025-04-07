package nro.server.service.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nro.server.service.core.player.PlayerService;
import nro.server.service.core.player.SkillService;
import nro.server.service.model.map.areas.Area;

import java.util.concurrent.locks.ReentrantReadWriteLock;

@Getter
@Setter
@NoArgsConstructor
public abstract class Entity {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    protected int id;
    private String name = "";
    private int typeObject;

    private byte gender;
    private byte typePk;
    private int teleport = 0;
    private short mount = -1;

    protected short x;
    protected short y;

    protected Points points;
    protected Skills skills;
    protected Fusion fusion;
    protected Fashion fashion;
    protected Area area;

    private static final short[][] BIRD_FRAMES = {
            {281, 361, 351},
            {512, 513, 536},
            {514, 515, 537}
    };

    private static final String[][] BIRD_NAMES = {
            {"Puaru"}, {"Piano"}, {"Icarus"}
    };

    public short[] getPlayerBirdFrames() {
        var gender = this.getGender();
        if (gender < 0 || gender >= BIRD_FRAMES.length) {
            gender = 2;
        }
        return BIRD_FRAMES[gender];
    }

    public String[] getPlayerBirdNames() {
        var gender = this.getGender();
        if (gender < 0 || gender >= BIRD_NAMES.length) {
            gender = 2;
        }
        return BIRD_NAMES[gender];
    }

    public void changeTypePlayerKill(int typePk) {
        if (typePk < 0 || typePk > 3) return;
        if (this.typePk == typePk) return;
        this.typePk = (byte) typePk;
        PlayerService.getInstance().sendEntityChangerTypePlayerKill(this);
    }

    public long handleAttack(Entity entityAttack, Entity entityTarget, int type, long damage) {
        this.lock.writeLock().lock();
        try {
            if (this.points.isDead()) {
                this.points.setDie();
                return 0;
            }
            this.points.subCurrentHp(damage);

            if (this.points.isDead()) {
                this.points.setDie();
                return 0;
            }
            SkillService.getInstance().sendEntityAttackEntity(entityAttack, entityTarget, damage, true);
            return damage;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public abstract void dispose();

}
