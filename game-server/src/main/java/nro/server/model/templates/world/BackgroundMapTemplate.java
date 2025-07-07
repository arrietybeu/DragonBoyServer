package nro.server.model.templates.world;

import lombok.Data;

/**
 * @author Arriety
 */
@Data
public class BackgroundMapTemplate {

    private int id;
    private short image;
    private byte layer;
    private short dx;
    private short dy;

}
