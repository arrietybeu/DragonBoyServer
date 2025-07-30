package nro.server.engine.quest;

import nro.server.data_holders.GameEngine;
import nro.server.data_holders.YamlDataLoader;
import nro.server.model.templates.task.QuestStep;
import nro.server.model.templates.task.QuestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Arriety
 */
public class QuestEngine implements GameEngine {

    private static final Logger log = LoggerFactory.getLogger(QuestEngine.class);

    private static final Map<Integer, QuestTemplate> QUESTS = new HashMap<>();

    @Override
    public void init() throws Throwable {
        log.info("Loading quest templates from folder: resources/data_holder/task");

        File folder = new File("resources/data_holder/task");
        if (!folder.exists() || !folder.isDirectory()) {
            throw new RuntimeException("Quest folder does not exist: " + folder.getAbsolutePath());
        }

        Pattern fileNamePattern = Pattern.compile("task_(\\d+)\\.yml");

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            Matcher matcher = fileNamePattern.matcher(file.getName());
            if (matcher.matches()) {
                int taskId = Integer.parseInt(matcher.group(1));
                QuestTemplate template = YamlDataLoader.loadObject(file.getPath(), QuestTemplate.class);
                QUESTS.put(taskId, template);
                log.info("Loaded task ID {} from file {}", taskId, file.getName());
            }
        }

        log.info("Total quest templates loaded: {}", QUESTS.size());
    }

    @Override
    public void reload() throws Throwable {
        QUESTS.clear();
        init();
    }

    @Override
    public void clear() throws Throwable {
        QUESTS.clear();
    }

    public QuestTemplate getTask(int id) {
        return QUESTS.get(id);
    }

    private static final class SingletonHolder {
        private static final QuestEngine INSTANCE = new QuestEngine();
    }

    public static QuestEngine getInstance() {
        return QuestEngine.SingletonHolder.INSTANCE;
    }

    public void logTask0Info() {
        log.info("===== QUEST TASK INFO =====");
        for (QuestTemplate quest : QUESTS.values()) {
            if (quest == null) {
                log.warn("TASK_0 is not loaded.");
                return;
            }


            log.info("ID: {}", quest.id);

            // Title theo gender
            for (int i = 0; i < quest.title.size(); i++) {
                log.info("Title[{}]: {}", i, quest.title.get(i));
            }

            // Detail theo gender
            for (int i = 0; i < quest.detail.size(); i++) {
                log.info("Detail[{}]: {}", i, quest.detail.get(i));
            }

            // Steps
            if (quest.steps != null) {
                for (QuestStep step : quest.steps) {
                    log.info("  --- Step {} ---", step.index);

                    if (step.name != null) {
                        for (int i = 0; i < step.name.size(); i++) {
                            log.info("    Name[{}]: {}", i, step.name.get(i));
                        }
                    }

                    if (step.detail != null) {
                        for (int i = 0; i < step.detail.size(); i++) {
                            log.info("    Detail[{}]: {}", i, step.detail.get(i));
                        }
                    }

                    if (step.npc_id != null) {
                        for (int i = 0; i < step.npc_id.size(); i++) {
                            log.info("    NpcId[{}]: {}", i, step.npc_id.get(i));
                        }
                    }

                    if (step.map_id != null) {
                        for (int i = 0; i < step.map_id.size(); i++) {
                            log.info("    MapId[{}]: {}", i, step.map_id.get(i));
                        }
                    }

                    log.info("    Count: {}", step.count);

                    if (step.reward != null && !step.reward.isEmpty()) {
                        for (int i = 0; i < step.reward.size(); i++) {
                            QuestStep.ItemReward reward = step.reward.get(i);
                            log.info("    Reward[{}]: item_id = {}, quantity = {}", i, reward.item_id, reward.quantity);
                        }
                    } else {
                        log.info("    Reward: None");
                    }
                }
            }

            log.info("===== END TASK_0 INFO =====");
        }
    }

}
