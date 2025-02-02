package nro.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public abstract class LiveObject {

    private int id;
    private String name;
    private byte typeObject;

    private short aura;
    private byte idEffSetItem;
    private short idHat;

    private byte gender;
    private byte typePk;

    private short x;
    private short y;


}
