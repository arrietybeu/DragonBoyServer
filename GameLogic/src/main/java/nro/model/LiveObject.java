package nro.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class LiveObject {

    private int id;
    private String name;
    private byte typeObject;

    private short x;
    private short y;

}
