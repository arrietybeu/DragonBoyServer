package nro.server.model.ecs.component.player;

import com.artemis.Component;
import nro.server.network.nro.NroConnection;

/**
 * @author Arriety
 */
public class PlayerComponent extends Component {

    public NroConnection connection;

    public PlayerComponent() {
    }

    public PlayerComponent(NroConnection conn) {
        System.out.println("kho chua vai ca lon");
        this.connection = conn;
    }

    public boolean isOnline() {
        return connection != null;
    }

}
