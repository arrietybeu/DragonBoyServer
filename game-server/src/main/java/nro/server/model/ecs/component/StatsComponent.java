package nro.server.model.ecs.component;


import com.artemis.Component;

/**
 * @author Arriety
 */
public class StatsComponent extends Component {

    public long power;
    public long potential;

    // Chỉ số gốc
    public int baseHp;
    public int baseMp;
    public int baseDamage;
    public int baseDefense;
    public byte baseCrit;

    public short expPerStatIncrease;
    public long currentDamage;

    // Các chỉ số sau khi tính toán (sẽ được cập nhật bởi một System)
    public long totalDefense;
    public byte totalCriticalChance;
    public byte movementSpeed = 5;

    public byte giamST;
    public short cCritDameFull;

    public int activePoint;

    public StatsComponent() {
        // Default constructor for Artemis
    }

    public StatsComponent(long power, long potential,
                          int baseHp, int baseMp, int baseDamage,
                          int baseDefense, byte baseCrit, int activePoint) {
        this.power = power;
        this.potential = potential;
        this.baseHp = baseHp;
        this.baseMp = baseMp;
        this.baseDamage = baseDamage;
        this.baseDefense = baseDefense;
        this.baseCrit = baseCrit;
        this.activePoint = activePoint;
    }

}