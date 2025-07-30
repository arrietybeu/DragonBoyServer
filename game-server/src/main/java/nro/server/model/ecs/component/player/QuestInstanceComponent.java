package nro.server.model.ecs.component.player;

import com.artemis.PooledComponent;


/**
 * @author Arriety
 */
public class QuestInstanceComponent extends PooledComponent {

    public int questId;
    public int currentStep;
    public int currentCount;
    public boolean completed = false;

    @Override
    public void reset() {
        questId = 0;
        currentStep = 0;
        currentCount = 0;
        completed = false;
    }
}
