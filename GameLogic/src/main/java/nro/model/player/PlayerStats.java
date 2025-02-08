package nro.model.player;

import lombok.Data;

@Data
public class PlayerStats {

    private final Player player;

    // chi so co ban
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
    private long potentialPoints;
    private short expPerStatIncrease;
    private byte hpPer1000Potential;
    private byte mpPer1000Potential;
    private byte damagePer1000Potential;

    // power level
    private long power;

    public PlayerStats(Player player) {
        this.player = player;
    }
}
