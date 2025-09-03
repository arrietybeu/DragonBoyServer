package nro.server.engine.quest.system;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import nro.server.engine.quest.QuestEngine;
import nro.server.model.ecs.component.InfoComponent;
import nro.server.model.ecs.component.player.QuestInstanceComponent;
import nro.server.model.templates.task.QuestStep;
import nro.server.model.templates.task.QuestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arriety
 */
public class QuestSystem extends BaseEntitySystem {

    private static final Logger log = LoggerFactory.getLogger(QuestSystem.class);

    protected ComponentMapper<QuestInstanceComponent> mQuestInstance;
    protected ComponentMapper<InfoComponent> mInfo;

    public QuestSystem() {
        super(Aspect.all(QuestInstanceComponent.class, InfoComponent.class));
    }

    @Override
    protected void processSystem() {
        int[] entities = getEntityIds().getData();

        int size = getEntityIds().size();

        for (int i = 0; i < size; i++) {
            int entityId = entities[i];

            QuestInstanceComponent q = mQuestInstance.get(entityId);
            if (q.completed)
                continue;

            QuestTemplate quest = QuestEngine.getInstance().getTask(q.questId);
            if (quest == null)
                continue;

            if (q.currentStep >= quest.steps.size()) {
                q.completed = true;
                log.info("Player {} hoàn thành nhiệm vụ {}", entityId, q.questId);
                continue;
            }

            QuestStep step = quest.steps.get(q.currentStep);
            if (q.currentCount >= step.count) {
                q.currentStep++;
                q.currentCount = 0;

                if (q.currentStep >= quest.steps.size()) {
                    q.completed = true;
                    log.info("Player {} hoàn thành toàn bộ nhiệm vụ {}", entityId, q.questId);
                } else {
                    log.info("Player {} chuyển sang bước {}", entityId, q.currentStep);
                }
            }
        }
    }
}
