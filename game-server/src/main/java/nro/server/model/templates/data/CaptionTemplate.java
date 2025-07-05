package nro.server.model.templates.data;

/**
 * @author Arriety
 */
public record CaptionTemplate(int id, long exp) {

    public record CaptionLevel(int id, byte gender, String name) {
    }
}
