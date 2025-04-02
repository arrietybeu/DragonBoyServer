package nro.server.service.model.entity.ai.boss;

import nro.server.service.model.entity.Points;
import nro.server.service.model.entity.monster.Monster;
import nro.server.service.model.template.item.ItemOption;

public class BossPoints extends Points {

    @Override
    public long getDameAttack() {
        return 0;
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
        return 0;
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
