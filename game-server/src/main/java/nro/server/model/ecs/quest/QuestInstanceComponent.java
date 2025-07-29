package nro.server.model.ecs.quest;

import com.artemis.Component;

/**
 * @author Arriety
 */
public class QuestInstanceComponent extends Component {
    public int templateId;
    public int stepIndex;
    public boolean completed;
}