package nro.server.model.templates.data;

public record PartTemplate(int type, PartImage[] pi) {
    public record PartImage(short id, byte dx, byte dy) {
    }
}
