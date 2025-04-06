package nro.server.service.model.entity.ai.boss;

import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.Points;
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
    public long getDameAttack() {
        return super.getDameAttack();
    }

    @Override
    public void calculateStats() {
    }

    @Override
    public void resetBaseStats() {
    }

    @Override
    public void reduceMPWhenFlying() {
    }

    @Override
    public void applyItemBonuses() {
    }

    @Override
    public long getParamOption(long currentPoint, ItemOption option) {
        return 0;
    }

    @Override
    public long getDameSkill() {
        return super.getDameSkill();
    }

    @Override
    public void addExp(int type, long exp) {
    }

    @Override
    public void subExp(int type, long exp) {
    }

    @Override
    public void returnTownFromDead() {
    }

    @Override
    public void setDie() {
    }

    @Override
    public void setLive() {
    }

    @Override
    public void upPotentialPoint(int type, int point) {
    }

    @Override
    public boolean isUpgradePotential(int type, long potentiaUse, long currentPoint, int point) {
        return false;
    }

    @Override
    public void healPlayer() {
    }

    @Override
    public long getPotentialPointsAttack(Monster monster, long damage) {
        return 0;
    }
}
