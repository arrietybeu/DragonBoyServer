package nro.server.model.templates.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Arriety
 */
public record MonsterTemplate(
        int id,
        byte type,
        @JsonProperty("NAME") String NAME,
        long damage,
        long hp,
        @JsonProperty("range_move") byte rangeMove,
        byte speed,
        @JsonProperty("dart_type") byte dartType
) {
}
