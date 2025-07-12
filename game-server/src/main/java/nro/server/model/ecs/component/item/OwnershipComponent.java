package nro.server.model.ecs.component.item;

import com.artemis.Component;

/**
 * @author Arriety
 */
public class OwnershipComponent extends Component {

    public int ownerEntityId;
    public ItemLocation location;

    public OwnershipComponent() {}

    public OwnershipComponent(int ownerEntityId, ItemLocation location) {
        this.ownerEntityId = ownerEntityId;
        this.location = location;
    }
}