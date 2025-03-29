package nro.service.model.item;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemTime {

    public short iconId;

    public int second;

    public int minute;

    private long curr;

    private long last;

    private boolean isText;

    private boolean dontClear;

    private String text;

    private boolean isPaint_coolDownBar;

    public int time;

    public int coutTime;

    private int per = 100;

}
