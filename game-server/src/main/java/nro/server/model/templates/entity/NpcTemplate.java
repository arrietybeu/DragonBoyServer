package nro.server.model.templates.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Arriety
 */
public record NpcTemplate(int id,
                          @JsonProperty("NAME")
                          String name,
                          int head,
                          int body, int leg,
                          @JsonProperty("avatar_id")
                          short avatarId, String chat) {
    public record NpcInfo(int npcId, int x, int y,
                          int status, int avatar) {
    }
}
