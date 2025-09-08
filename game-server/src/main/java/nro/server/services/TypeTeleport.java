package nro.server.services;

public enum TypeTeleport {

    DEFAULT(0),
    SPACE_SHIP_FOR_GENDER(1),
    TELEPORT(2),
    SPACE_TENIS(3);

    final int value;

    TypeTeleport(int value) {
        this.value = value;
    }

    public byte getValue() {
        return (byte) value;
    }

}
