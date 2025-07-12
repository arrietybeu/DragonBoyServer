package nro.server.model.ecs.component.item;

import com.artemis.Component;

/**
 * @author Arriety
 */
public class DatabaseIdComponent extends Component {
    public int dbId;

    public DatabaseIdComponent() {}

    public DatabaseIdComponent(int dbId) {
        this.dbId = dbId;
    }
}