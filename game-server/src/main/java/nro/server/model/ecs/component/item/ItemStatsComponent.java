package nro.server.model.ecs.component.item;

import com.artemis.Component;
import nro.server.model.item.ItemOptionData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arriety
 */
public class ItemStatsComponent extends Component {

    public List<ItemOptionData> options = new ArrayList<>();

}
