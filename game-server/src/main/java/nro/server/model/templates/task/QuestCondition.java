package nro.server.model.templates.task;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Arriety
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = KillMobCondition.class, name = "kill_mob"),
        @JsonSubTypes.Type(value = TalkNpcCondition.class, name = "talk_npc")
})
public abstract class QuestCondition {
    public String type;
    public abstract String getKey(); // unique key để theo dõi progress
}