package nro.service.model.model.template;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaptionTemplate {

    private int id;
    private long exp;

    public record CaptionLevel(int id, byte gender, String name) {
    }
}
