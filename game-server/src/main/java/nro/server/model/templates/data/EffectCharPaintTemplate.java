package nro.server.model.templates.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EffectCharPaintTemplate {

    private int idEf;
    private EffectInfoPaint[] arrEfInfo;

    public record EffectInfoPaint(int dx, int dy, int idImg) {
    }

}
