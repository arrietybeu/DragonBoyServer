package nro.model.player;

import lombok.Getter;
import lombok.Setter;
import nro.service.PlayerService;

@Getter
@Setter
public class PlayerPoints {

    private final Player player;

    // chỉ số cơ bản, chỉ số gốc
    private int baseHP;
    private int baseMP;
    private int baseDamage;
    private int baseDefense;
    private byte baseCriticalChance;
    private byte movementSpeed;
    private short stamina;

    // chi so hien tai
    private long currentHP;
    private long currentMP;

    // chi so max
    private long maxHP;
    private long maxMP;
    private short maxStamina;

    // chi so tong hop cam lon
    private long totalDamage;
    private long totalDefense;
    private byte totalCriticalChance;

    // tnsm
    private long potentialPoints;//  tiem nang
    private short expPerStatIncrease;
    private byte hpPer1000Potential;
    private byte mpPer1000Potential;
    private byte damagePer1000Potential;

    private short eff5BuffHp;
    private short eff5BuffMp;

    // power level
    private long power;

    public PlayerPoints(Player player) {
        this.player = player;
    }

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

    public void setDie() {
        // TODO xử lý khi chết
        // set lai location
        // lock move,
        // cancel trade
        // huy skill
        this.currentHP = 0;

        this.player.getPlayerStatus().setLockMove(true);
        PlayerService playerService = PlayerService.getInstance();
        playerService.sendCurrencyHpMp(this.player);
        playerService.sendPlayerDie(this.player);
        playerService.sendPlayerDeathToArea(this.player);
    }

    public void setLive() {
        this.currentHP = this.maxHP;
        this.player.getPlayerStatus().setLockMove(false);
        PlayerService playerService = PlayerService.getInstance();
        playerService.sendCurrencyHpMp(this.player);
        playerService.sendPlayerRevive(this.player);
        playerService.sendPlayerReviveToArea(this.player);
    }

    @Override
    public String toString() {
        return "PlayerPoints{" +
                "player=" + player +
                ", baseHP=" + baseHP +
                ", baseMP=" + baseMP +
                ", baseDamage=" + baseDamage +
                ", baseDefense=" + baseDefense +
                ", baseCriticalChance=" + baseCriticalChance +
                ", movementSpeed=" + movementSpeed +
                ", stamina=" + stamina +
                ", currentHP=" + currentHP +
                ", currentMP=" + currentMP +
                ", maxHP=" + maxHP +
                ", maxMP=" + maxMP +
                ", maxStamina=" + maxStamina +
                ", totalDamage=" + totalDamage +
                ", totalDefense=" + totalDefense +
                ", totalCriticalChance=" + totalCriticalChance +
                ", potentialPoints=" + potentialPoints +
                ", expPerStatIncrease=" + expPerStatIncrease +
                ", hpPer1000Potential=" + hpPer1000Potential +
                ", mpPer1000Potential=" + mpPer1000Potential +
                ", damagePer1000Potential=" + damagePer1000Potential +
                ", eff5BuffHp=" + eff5BuffHp +
                ", eff5BuffMp=" + eff5BuffMp +
                ", power=" + power +
                '}';
    }

}
