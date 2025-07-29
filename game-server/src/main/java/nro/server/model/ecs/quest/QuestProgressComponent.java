package nro.server.model.ecs.quest;

import com.artemis.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arriety
 */
public class QuestProgressComponent extends Component {
    public Map<String, Integer> progress = new HashMap<>();
}