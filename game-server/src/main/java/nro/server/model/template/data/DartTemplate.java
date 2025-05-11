package nro.server.model.template.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DartTemplate {

    private int id;

    @JsonProperty("nUpdate")
    private int nUpdate;

    private int va;

    private int xdPercent;

    private int[] tail;

    private int[] tailBorder;

    private int[] xd1;

    private int[] xd2;

    private int[][] head;

    private int[][] headBorder;

    private int loop;
}
