package nro.server.service.model.entity.ai.boss;

import nro.server.service.core.player.PlayerService;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.Points;
import nro.server.service.model.entity.ai.AIState;
import nro.server.service.model.entity.monster.Monster;
import nro.server.service.model.template.item.ItemOption;

public class BossPoints extends Points {

    public BossPoints(Entity entity) {
        super(entity);
    }

    @Override
    public BossPoints copy(Entity boss) {
        BossPoints copy = new BossPoints(boss);
        copy.setBaseHP(this.baseHP);
        copy.setBaseMP(this.baseMP);
        copy.setBaseDamage(this.baseDamage);
        copy.setBaseDefense(this.baseDefense);
        copy.setMovementSpeed(this.movementSpeed);
        copy.setMaxHP(copy.getBaseHP());
        copy.setMaxMP(copy.getBaseMP());
        copy.setCurrentHp(copy.getMaxHP());
        copy.setCurrentMp(copy.getMaxMP());
        copy.setCurrentDamage(copy.getBaseDamage());
        return copy;
    }

    @Override
    public void calculateStats() {
        System.out.println("BossPoints.calculateStats");
    }

    @Override
    public void resetBaseStats() {
        System.out.println("BossPoints.resetBaseStats");
    }

    @Override
    public void reduceMPWhenFlying() {
        System.out.println("BossPoints.reduceMPWhenFlying");
    }

    @Override
    public void applyItemBonuses() {
        System.out.println("BossPoints.applyItemBonuses");
    }

    @Override
    public long getParamOption(long currentPoint, ItemOption option) {
        return 0;
    }

    @Override
    public void addExp(int type, long exp) {
        System.out.println("BossPoints.addExp");
    }

    @Override
    public void subExp(int type, long exp) {
        System.out.println("BossPoints.subExp");
    }

    @Override
    public void returnTownFromDead() {
        System.out.println("BossPoints.returnTownFromDead");
    }

    @Override
    public void setDie(Entity killer) {
        this.currentHP = 0;
        if (owner instanceof Boss boss) {
            boss.setLockMove(true);
            boss.setState(AIState.DEAD);
            boss.onDie(killer);

            PlayerService playerService = PlayerService.getInstance();
            playerService.sendPlayerDeathToArea(boss);
        }
    }

    @Override
    public void setLive() {
        System.out.println("BossPoints.setLive");
    }

    @Override
    public void upPotentialPoint(int type, int point) {
        System.out.println("BossPoints.upPotentialPoint");
    }

    @Override
    public boolean isUpgradePotential(int type, long potentiaUse, long currentPoint, int point) {
        return false;
    }

    @Override
    public void healPlayer() {
        System.out.println("BossPoints.healPlayer");
    }

    @Override
    public long getPotentialPointsAttack(Monster monster, long damage) {
        return 0;
    }
}
