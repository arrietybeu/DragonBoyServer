package nro.server.model.templates.task;

import java.util.List;

/**
 * @author Arriety
 */
public class QuestTemplate {

    public int id;
    public List<String> title;
    public List<String> detail;
    public List<QuestStep> steps;
}
