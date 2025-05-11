package nro.server.model.template.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ArrowPaintTemplate {

    private int id;
    private int life;
    private int ax;
    private int ay;
    private int axTo;
    private int ayTo;
    private int avx;
    private int avy;
    private int adx;
    private int ady;
    private int[] imgId;
}
