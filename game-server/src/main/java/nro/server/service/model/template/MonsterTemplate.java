package nro.server.service.model.template;

public record MonsterTemplate(int id, byte type, String name, long damage, long hp, byte rangeMove, byte speed,
                              byte dartType) {
}
