package nro.server.model.ecs.component.item;

import com.artemis.Component;

/**
 * @author Arriety
 */
public class ItemInfoComponent extends Component {

    public int templateId;
    public int quantity;

    public ItemInfoComponent() {}

    public ItemInfoComponent(int templateId, int quantity) {
        this.templateId = templateId;
        this.quantity = quantity;
    }
}