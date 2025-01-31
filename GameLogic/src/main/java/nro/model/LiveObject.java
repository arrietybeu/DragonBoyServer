package nro.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public abstract class LiveObject {

    private int id;
    private String name;
    private byte typeObject;

    private byte gender;
    private byte typePk;

    private short x;
    private short y;


}
