package nro.server.model.templates.data;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Arriety
 */
public record PartTemplate(int id, int type, PartImage[] data) {

    public record PartImage(
            @JsonProperty("icon_id")
            short icon, byte dx, byte dy) {
    }
}
