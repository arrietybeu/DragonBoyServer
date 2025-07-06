package nro.server.model.item;

/**
 * @author Arriety
 */
public record ItemOption(int id, int param, short type) {

    public ItemOption() {
        this(73, 0, (short) 0);
    }
}
