package nro.server.service.model.entity.bot;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BotData {

    private int gender;
    private int maxHp;
    private int pkType;

    private int head;
    private int body;
    private int leg;
    private int flagBag;

    public BotData() {

    }

}
