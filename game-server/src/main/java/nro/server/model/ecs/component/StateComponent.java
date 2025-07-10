package nro.server.model.ecs.component;

import com.artemis.Component;

/**
 * @author Arriety
 */
public class StateComponent extends Component {
    public byte pkFlag = 0; // Cờ PK
    public boolean isNewPlayer = true;
    public boolean isMonkey = false;
}
