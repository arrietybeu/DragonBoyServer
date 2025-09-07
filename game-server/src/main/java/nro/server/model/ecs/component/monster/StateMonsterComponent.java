package nro.server.model.ecs.component.monster;

import com.artemis.Component;

/**
 * @author Arriety
 */
public class StateMonsterComponent extends Component {

    public byte status;

    public StateMonsterComponent() {
        status = 5;
    }

}
