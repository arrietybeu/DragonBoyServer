package nro.server.model.ecs.component.player;

import com.artemis.Component;

/**
 * @author Arriety
 */
public class TaskComponent extends Component {
    public int taskId;
    public int taskIndex;
    public int taskCount;

    public TaskComponent() {
    }

    public TaskComponent(int taskId, int taskIndex, int taskCount) {
        this.taskId = taskId;
        this.taskIndex = taskIndex;
        this.taskCount = taskCount;
    }
}
