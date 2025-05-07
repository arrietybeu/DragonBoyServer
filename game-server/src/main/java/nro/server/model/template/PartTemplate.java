package nro.server.model.template;

public record PartTemplate(short id, byte type, PartImageTemplate[] pi) {
    public record PartImageTemplate(short icon, byte dx, byte dy) {
    }
}
