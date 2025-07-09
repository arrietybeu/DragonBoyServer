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

    public HealthComponent(long currentHP, long maxHP, long currentMP, long maxMP) {
        this.currentHP = currentHP;
        this.maxHP = maxHP;
        this.currentMP = currentMP;
        this.maxMP = maxMP;
    }
}