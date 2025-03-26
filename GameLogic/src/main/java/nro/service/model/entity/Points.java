package nro.service.model.entity;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstOption;
import nro.consts.ConstPlayer;
import nro.consts.ConstSkill;
import nro.service.model.entity.boss.Boss;
import nro.service.model.entity.player.Player;
import nro.service.model.item.Item;
import nro.service.model.template.item.ItemOption;
import nro.service.model.map.GameMap;
import nro.service.model.entity.monster.Monster;
import nro.service.model.template.entity.SkillInfo;
import nro.server.system.LogServer;
import nro.server.config.ConfigServer;
import nro.server.manager.CaptionManager;
import nro.server.manager.ItemManager;
import nro.server.manager.MapManager;
import nro.service.core.map.AreaService;
import nro.service.core.player.PlayerService;
import nro.service.core.system.ServerService;
import nro.utils.Util;

import java.util.List;

@Getter
@Setter
public abstract class Points {

    // chỉ số cơ bản, chỉ số gốc
    protected int baseHP, baseMP;
    protected int baseDamage;
    protected int baseDefense;
    protected byte baseCriticalChance;
    protected byte movementSpeed;
    protected short stamina;

    // chi so hien tai
    protected long currentHP, currentMP, currentDamage;

    // chi so
    protected long maxHP, maxMP;
    protected short maxStamina;

    // chi so tong hop cam lon
    protected long totalDefense;
    protected byte totalCriticalChance;

    protected short expPerStatIncrease;
    protected byte hpPer1000Potential;
    protected byte mpPer1000Potential;
    protected byte damagePer1000Potential;

    protected short eff5BuffHp, eff5BuffMp;

    // power , tiem nang
    protected long power, potentialPoints;

    protected short tlHutHp, tlHutMp, tlHutHpMob;

    protected int percentExpPotentia;

    protected boolean isHaveMount;

    public boolean isDead() {
        return this.currentHP <= 0;
    }

    public void setCurrentHp(long hp) {
        if (hp < 0) {
            this.currentHP = 0;
        } else this.currentHP = Math.min(hp, this.maxHP);
    }

    public void setCurrentMp(long mp) {
        if (mp < 0) {
            this.currentMP = 0;
        } else this.currentMP = Math.min(mp, this.maxMP);
    }

    public void subCurrentHp(long hp) {
        this.currentHP -= hp;
        if (this.currentHP < 0) {
            this.currentHP = 0;
        }
    }

    public abstract long getDameAttack();

    public abstract void calculateStats();

    public abstract void resetBaseStats();

    public abstract void reduceMPWhenFlying();

    public abstract void applyItemBonuses();

    public abstract long getParamOption(long currentPoint, ItemOption option);

    public abstract long getDameSkill();

    public abstract void addExp(int type, long exp);

    public abstract void subExp(int type, long exp);

    public abstract void returnTownFromDead();

    public abstract void setDie();

    public abstract void setLive();

    public abstract void upPotentialPoint(int type, int point);

    public abstract boolean isUpgradePotential(int type, long potentiaUse, final long currentPoint, int point);

    public abstract void healPlayer();

    public abstract long getPotentialPointsAttack(Monster monster, long damage);

}
