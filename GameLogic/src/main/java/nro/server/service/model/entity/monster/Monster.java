package nro.server.service.model.entity.monster;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstPlayer;
import nro.consts.ConstTypeObject;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.discpile.Disciple;
import nro.server.service.model.item.ItemMap;
import nro.server.service.model.map.areas.Area;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;
import nro.server.service.core.combat.MonsterService;
import nro.server.service.core.player.SkillService;
import nro.server.service.core.item.DropItemMap;
import nro.utils.Util;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Getter
@Setter
public class Monster extends Entity {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final int templateId;
    private final MonsterPoint point;
    private final MonsterStatus status;
    private final MonsterInfo info;
    private final Area area;

    private final Set<Player> attackers = new LinkedHashSet<>();

    public Monster(int templateId, int id, long maxHp, byte level, short x, short y, Area area) {
        this.setTypeObject(ConstTypeObject.TYPE_MONSTER);
        this.templateId = templateId;
        this.setId(id);
        this.setX(x);
        this.setY(y);
        this.point = new MonsterPoint(this, maxHp, level);
        this.info = new MonsterInfo(this);
        this.status = new MonsterStatus(this);
        this.area = area;
    }

//    @Override
//    public void update() {
//        if (this.point.isDead()) {
//            if (Util.canDoWithTime(this.info.getLastTimeDie(), 5000)) {
//                this.setLive();
//            }
//        } else {
//            if (Util.canDoWithTime(this.info.getLastTimeAttack(), 1000)) {
//                this.attackPlayer();
//            }
//        }
//    }

    @Override
    public long handleAttack(final Player plAttack, int type, long damage) {
        this.lock.writeLock().lock();
        try {
            if (this.point.isDead()) return 0;

            this.attackers.add(plAttack);

            // Kiem tra dame
            if (type != 1) {
                // mộc nhân thì giới hạn dame là 10
                if (this.templateId == 0 && damage >= 10) damage = 10;

                // nếu dame lớn hơn máu của quái thì khi trừ máu của quái, quái sẽ còn 1 máu tránh sốc dame lớn
                if (this.point.getHp() == this.point.getMaxHp() && damage >= this.point.getHp()) {
                    damage = this.point.getHp() - 1;
                }
            }

            // send effect skill attack monster
            SkillService.getInstance().sendEntityAttackMonster(plAttack, this.getId());

            if (damage >= this.point.getHp()) damage = this.point.getHp();

            // tru hp cua monster
            this.point.subHp(damage);

            this.handleCreateExpEntityAttackMob(plAttack, damage);

            // kiem tra monster chet
            if (this.point.isDead()) {
                this.setDie(plAttack, damage);
                plAttack.getPlayerTask().checkDoneTaskKKillMonster(this);
            } else {
                boolean isHutHp = plAttack.getPoints().getTlHutHpMob() > 0;
                MonsterService.getInstance().sendHpMonster(plAttack, this, damage, true, isHutHp);
            }
            return damage;
        } catch (Exception exception) {
            LogServer.LogException("Monster handleAttack: " + exception.getMessage(), exception);
            return 0;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void setLive() {
        try {
            this.point.setDead(false);
            this.status.setStatus((byte) 5);
            this.info.setLevelBoss((byte) 0);
            this.point.setHp(this.point.getMaxHp());
            MonsterService.getInstance().sendMonsterRevice(this);
        } catch (Exception e) {
            LogServer.LogException("Monster setLive: " + e.getMessage(), e);
        }
    }

    public void setDie(Player plAttack, long damage) {
        try {
            this.point.setHp(0);
            this.point.setDead(true);
            this.status.setStatus((byte) 0);
            this.info.setLastTimeDie(System.currentTimeMillis());
            final List<ItemMap> itemMaps = DropItemMap.getInstance().dropItemMapForMonster(plAttack, this);
            boolean isCritical = plAttack.getPoints().getTotalCriticalChance() == 1;
            MonsterService.getInstance().sendMonsterDie(this, damage, isCritical, itemMaps);
        } catch (RuntimeException ex) {
            LogServer.LogException("Monster setDie: " + ex.getMessage(), ex);
        }
    }

    private void attackPlayer() {
        if (this.isMonsterAttack()) {
            try {
                Player player = this.playerCanAttack();
                this.attack(player);
            } catch (Exception exception) {
                LogServer.LogException("Monster attackPlayer: " + exception.getMessage(), exception);
            }
        }
    }

    private void attack(Player player) {
        if (player != null) {
            long dame = this.constDame(player);
            MonsterService monsterService = MonsterService.getInstance();
            monsterService.sendMonsterAttackMe(this, player, dame, -1);
            monsterService.sendMonsterAttackPlayer(this, player, -1);
        }
        this.info.setLastTimeAttack(System.currentTimeMillis());
    }

    private long constDame(Player player) {
        // TODO bù trừ dame
        long dame = this.point.getDameGoc();
        return player.handleAttack(player, 0, dame);
    }

    private boolean isMonsterAttack() {
        return this.info.getType() != 0 && this.status.getStatus() != 0 && !this.point.isDead();
    }

    private Player playerCanAttack() {
        for (Player player : this.area.getPlayersByType(ConstTypeObject.TYPE_PLAYER)) {
            if (player == null) continue;
            if (player.getPoints().isDead()) continue;
            if (Util.getDistance(this.getX(), this.getY(), player.getX(), player.getY()) < 80) {
                return player;
            }
        }
        return null;
    }

    private void handleCreateExpEntityAttackMob(Entity entity, long damage) {
        var ms = System.currentTimeMillis();
        try {
            switch (entity) {
                case Player player -> {
                    if (player.getPlayerStatus().getLastTimeAddExp() + 1000 > ms) return;
                    long exp = player.getPoints().getPotentialPointsAttack(this, damage);
                    player.getPoints().addExp(ConstPlayer.ADD_POWER_AND_EXP, exp);
                    player.getPlayerTask().checkDoneTaskUpgradeExp(player.getPoints().getPower());
                    player.getPlayerStatus().setLastTimeAddExp(ms);
                }
                case Disciple disciple -> {
                }
                default -> throw new IllegalStateException("Unexpected value: " + entity.getName());
            }
        } catch (Exception e) {
            LogServer.LogException("Monster handleCreateExpEntityAttackMob: " + e.getMessage(), e);
        }
    }

    @Override
    public void dispose() {
    }

}
