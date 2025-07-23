package nro.server.model.ecs.component;


import com.artemis.Component;

/**
 * @author Arriety
 */
public class HealthComponent extends Component {

    public long currentHP;
    public long maxHP;
    public long currentMP;
    public long maxMP;

    public byte hpPer1000Potential;
    public byte mpPer1000Potential;
    public byte damagePer1000Potential;

    public HealthComponent() {
        // Default constructor for Artemis
    }

    public HealthComponent(long currentHP, long maxHP, long currentMP, long maxMP) {
        this.currentHP = currentHP;
        this.maxHP = maxHP;
        this.currentMP = currentMP;
        this.maxMP = maxMP;
    }

}