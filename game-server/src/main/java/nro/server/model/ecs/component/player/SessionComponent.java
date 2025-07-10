package nro.server.model.ecs.component.player;

import com.artemis.Component;
import nro.server.network.nro.NroConnection;

/**
 * @author Arriety
 */
public class SessionComponent extends Component {

    public NroConnection connection;

    public SessionComponent() {
    }

    public SessionComponent(NroConnection conn) {
        this.connection = conn;
    }
}
