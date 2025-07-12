package nro.server.model.ecs.component.item;

import lombok.Getter;

public enum ItemLocation {
    BODY(0), BAG(1), BOX(2);

    @Getter
    public final int type;

    ItemLocation(int type) {
        this.type = type;
    }
}