package nro.server.model.ecs.component.monster;

import com.artemis.Component;

/**
 * @author Arriety
 */
public class StateMonsterComponent extends Component {

    public byte status;

    public byte level;
    public byte levelBoss; // sieu quai hay sao ys
    public boolean isBoss;


    public StateMonsterComponent() {
    }

    public StateMonsterComponent(int level) {
        status = 5;
        this.level = (byte) level;
    }

}
