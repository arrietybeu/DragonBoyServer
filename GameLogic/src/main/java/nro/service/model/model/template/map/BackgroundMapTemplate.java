package nro.service.model.model.template.map;

import lombok.Data;

@Data
public class BackgroundMapTemplate {

    private int id;
    private short imageId;
    private byte layer;
    private short dx;
    private short dy;

    public int[] tileX;
    public int[] tileY;

}
