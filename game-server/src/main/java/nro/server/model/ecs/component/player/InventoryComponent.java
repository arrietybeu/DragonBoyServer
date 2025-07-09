package nro.server.model.ecs.component.player;


import com.artemis.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arriety
 */
public class InventoryComponent extends Component {

    public List<Integer> itemsBody = new ArrayList<>();
    public List<Integer> itemsBag = new ArrayList<>();
    public List<Integer> itemsBox = new ArrayList<>();

}
