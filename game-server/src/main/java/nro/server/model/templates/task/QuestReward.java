package nro.server.model.templates.task;

import com.fasterxml.jackson.annotation.*;

/**
 * @author Arriety
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ItemReward.class, name = "item"),
        @JsonSubTypes.Type(value = ExpReward.class, name = "exp")
})
public abstract class QuestReward {

    public String type;

    public abstract void apply(int playerId);
}