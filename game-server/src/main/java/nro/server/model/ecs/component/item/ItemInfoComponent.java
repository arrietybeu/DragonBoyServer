package nro.server.model.ecs.component.item;

import com.artemis.Component;

/**
 * @author Arriety
 */
public class ItemInfoComponent extends Component {

    public short templateId;
    public int quantity;
    public int creator_id = -1;

    public ItemInfoComponent() {
    }

    public ItemInfoComponent(int templateId, int quantity, int creator_id) {
        this.templateId = (short) templateId;
        this.quantity = quantity;
        this.creator_id = creator_id;
    }
}