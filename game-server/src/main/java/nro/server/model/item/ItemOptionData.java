package nro.server.model.item;

/**
 * @author Arriety
 */
public record ItemOptionData(int id, int param, short type) {

    public ItemOptionData() {
        this(73, 0, (short) 0);
    }
}
