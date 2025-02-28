package nro.model.monster;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstTypeObject;
import nro.model.LiveObject;
import nro.model.map.areas.Area;
import nro.model.player.Player;
import nro.server.LogServer;
import nro.service.MonsterService;
import nro.utils.Util;

@Getter
@Setter
public class Monster extends LiveObject {

    private final int templateId;
    private final MonsterStats stats;
    private final MonsterStatus status;
    private final MonsterInfo info;
    private final Area area;

    public Monster(int templateId, int id, long maxHp, byte level, short x, short y, Area area) {
        this.setTypeObject(ConstTypeObject.TYPE_MONSTER);
        this.templateId = templateId;
        this.setId(id);
        this.setX(x);
        this.setY(y);
        this.stats = new MonsterStats(this, maxHp, level);
        this.info = new MonsterInfo(this);
        this.status = new MonsterStatus(this);
        this.area = area;
    }

    @Override
    public void update() {
        if (this.stats.isDead()) {
            if (Util.canDoWithTime(this.info.getLastTimeDie(), 5000)) {
                this.setLive();
            }
        } else {
            if (this.isMonsterAttack()) {
                if (Util.canDoWithTime(this.info.getLastTimeAttack(), 2000)) {
                    this.attackPlayer();
                }
            }
        }
    }

    @Override
    public long handleAttack(Player plAttack, long damage) {
        if (this.stats.isDead()) return 0;
        this.stats.subHp(damage);

        if (this.stats.isDead()) {
            this.setDie(plAttack, damage);
        }
        return damage;
    }

    private boolean isMonsterAttack() {
        return this.status.getStatus() != 0 && this.status.getStatus() != 1 && !this.stats.isDead()
                && this.templateId != 0 && this.templateId != 76 && this.templateId != 94;
    }

    public void setLive() {
        this.stats.setDead(false);
        this.status.setStatus((byte) 5);
        this.info.setLevelBoss((byte) 0);
        this.stats.setHp(this.stats.getMaxHp());
        MonsterService.getInstance().sendMonsterRevice(this);
        System.out.println("quai hoi sink: " + this.getInfo().getName());
    }

    public void setDie(Player plAttack, long damage) {
        this.stats.setHp(0);
        this.stats.setDead(true);
        this.status.setStatus((byte) 0);
        this.info.setLastTimeDie(System.currentTimeMillis());
        MonsterService.getInstance().sendMonsterDie(plAttack, this, damage);
    }

    private void attackPlayer() {
        try {
            Player player = this.playerCanAttack();
            if (player != null) {
                long dame = this.constDame(player);
                MonsterService monsterService = MonsterService.getInstance();
                monsterService.sendMonsterAttackMe(this, player, dame, -1);
                monsterService.sendMonsterAttackPlayer(this, player, -1);
            }
            this.info.setLastTimeAttack(System.currentTimeMillis());
        } catch (Exception exception) {
            LogServer.LogException("Monster attackPlayer: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    private long constDame(Player player) {
        // TODO bù trừ dame
        long dame = this.stats.getDameGoc();

        return player.handleAttack(player, dame);
    }

    private Player playerCanAttack() {
        for (Player player : this.area.getPlayersByType(ConstTypeObject.TYPE_PLAYER)) {
            if (player == null) continue;
            if (player.getPlayerPoints().isDead()) continue;
            if (Util.getDistance(this.getX(), this.getY(), player.getX(), player.getY()) < 80) {
                return player;
            }
        }
        return null;
    }

    @Override
    public void dispose() {
    }
}
