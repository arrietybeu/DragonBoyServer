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

    public String getTitle(int gender) {
        if (title == null || title.isEmpty()) {
            return "No Title";
        }

        if (title.size() == 1) {
            return title.getFirst();
        }
        return title.get(gender);
    }

    public String getDetail(int gender) {
        if (detail == null || detail.isEmpty()) {
            return "No Detail";
        }

        if (detail.size() == 1) {
            return detail.getFirst();
        }
        return detail.get(gender);
    }


}
