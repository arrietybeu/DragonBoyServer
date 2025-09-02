package nro.server.model.map.zone;

/**
 * @author Arriety
 */
public enum ZoneType {

    NORMAL(0), OFFLINE(1), DUNGEON(2), EVENT(3);

    private final byte value;

    ZoneType(int type) {
        this.value = (byte) type;
    }

    public byte getValue() {
        return value;
    }

}
