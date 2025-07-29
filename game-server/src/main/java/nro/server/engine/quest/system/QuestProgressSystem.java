package nro.server.engine.quest.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import nro.server.model.ecs.quest.OwnerComponent;
import nro.server.model.ecs.quest.QuestInstanceComponent;
import nro.server.model.ecs.quest.QuestProgressComponent;

/**
 * @author Arriety
 */
public class QuestProgressSystem extends IteratingSystem {

    private ComponentMapper<QuestInstanceComponent> questMapper;
    private ComponentMapper<QuestProgressComponent> progressMapper;
    private ComponentMapper<OwnerComponent> ownerMapper;

    public QuestProgressSystem() {
        super(Aspect.all(QuestInstanceComponent.class, QuestProgressComponent.class));
    }

    @Override
    protected void process(int questEntityId) {
        QuestInstanceComponent instance = questMapper.get(questEntityId);
        QuestProgressComponent progress = progressMapper.get(questEntityId);
        OwnerComponent owner = ownerMapper.get(questEntityId);

        if (instance.completed) return;

        int kill = progress.progress.getOrDefault("killMob", 0);
        if (kill >= 5) {
            instance.completed = true;
            System.out.println("Quest " + instance.templateId + " của player " + owner.playerEntityId + " hoàn thành!");
        }
    }
}