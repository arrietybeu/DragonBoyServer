package nro.service.model.entity;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Fusion {

    private final BaseModel entity;
    private byte typeFusion;
    private long lastTimeFusion;

    public Fusion(BaseModel entity) {
        this.entity = entity;
    }
}
