package nro.server.model.ecs.component.player;


import com.artemis.Component;
import nro.server.engine.InventoryState;

import java.util.List;

/**
 * @author Arriety
 */
public class InventoryComponent extends Component {

    public List<Integer> itemsBody;
    public List<Integer> itemsBag;
    public List<Integer> itemsBox;

    public InventoryState state = InventoryState.IDLE;

}
