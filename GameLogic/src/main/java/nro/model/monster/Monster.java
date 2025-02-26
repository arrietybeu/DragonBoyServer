package nro.model.monster;

import lombok.Getter;
import lombok.Setter;
import nro.model.LiveObject;
import nro.model.map.areas.Area;
import nro.model.player.Player;
import nro.server.LogServer;
import nro.service.MonsterService;
import nro.utils.Util;

import java.util.Map;

@Getter
@Setter
public class Monster extends LiveObject {

    private final int templateId;
    private final MonsterStats stats;
    private final MonsterStatus status;
    private final MonsterInfo info;
    private final Area area;

    public Monster(int templateId, int id, long maxHp, byte level, short x, short y, Area area) {
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
                this.live(0);
                System.out.println("quai hoi sink: " + this.getInfo().getName());
            }
        } else {
            if (Util.canDoWithTime(this.info.getLastTimeAttack(), 2000)) {
                this.attackPlayer();
            }
        }
    }

    public void takeDamage(Player plAttack, long damage) {
        if (this.stats.isDead()) return;

        this.stats.subHp(damage);

//        MonsterService.getInstance().sendMonsterUpdateHP(this);

        if (this.stats.isDead()) {
            this.die();
            MonsterService.getInstance().sendMonsterDie(plAttack, this, damage);
        }
    }


    public void live(int level) {
        this.stats.setDead(false);
        this.status.setStatus((byte) 5);
        this.info.setLevelBoss((byte) level);
        this.stats.setHp(this.stats.getMaxHp());
        MonsterService.getInstance().sendMonsterRevice(this);
    }

    public void die() {
        this.stats.setHp(0);
        this.stats.setDead(true);
        this.status.setStatus((byte) 0);
        this.info.setLastTimeDie(System.currentTimeMillis());
    }

    private void attackPlayer() {
        try {
            Player player = this.playerCanAttack();
            if (player != null) {
                long dame = this.constDame(player);
                MonsterService monsterService = MonsterService.getInstance();
                monsterService.sendMonsterAttackMe(this, player, dame, 5);
                monsterService.sendMonsterAttackPlayer(this, player, 5);
            }
            this.info.setLastTimeAttack(System.currentTimeMillis());
        } catch (Exception exception) {
            LogServer.LogException("Monster attackPlayer: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    private long constDame(Player player) {
        return this.stats.getDameGoc();
    }

    private Player playerCanAttack() {
        Map<Integer, Player> players = this.area.getPlayers();
        for (Player player : players.values()) {
            if (player == null) continue;
            if (player.getPlayerPoints().isDead()) continue;
            if (Util.getDistance(this.getX(), this.getY(), player.getX(), player.getY()) < 80) {
                return player;
            }
        }
        return null;
    }

}
