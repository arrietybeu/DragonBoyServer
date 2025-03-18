package nro.model.item;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class FlagImage {

    private short id;
    private String name;
    private short[] iconEffect;
    private short icon;

    public FlagImage(int id, String name, short icon, short[] iconEffect) {
        this.id = (short) id;
        this.name = name;
        this.icon = icon;
        this.iconEffect = iconEffect;
    }
}
