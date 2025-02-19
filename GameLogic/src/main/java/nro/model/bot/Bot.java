package nro.model.bot;

import lombok.Getter;
import lombok.Setter;
import nro.model.LiveObject;

@Getter
@Setter
public abstract class Bot extends LiveObject {

    private final BotData botData;

    public Bot(int id, String botName, int gender, int pkType, int head, int body, int leg, int bag, int maxHp, int cx, int cy) {
        this.setTypeObject(2);' '
        this.setId(id);
        this.setName(botName);
        this.setGender((byte) gender);
        this.setTypePk((byte) pkType);
        this.botData = new BotData();
        this.botData.setHead(head);
        this.botData.setBody(body);
        this.botData.setLeg(leg);
        this.botData.setFlagBag(bag);
        this.botData.setMaxHp(maxHp);
        this.setX((short) cx);
        this.setY((short) cy);
    }

    @Override
    public void update() {
    }

}
