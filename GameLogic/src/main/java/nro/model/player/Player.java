package nro.model.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nro.model.LiveObject;
import nro.model.map.areas.Area;
import nro.network.Message;
import nro.network.Session;

@EqualsAndHashCode(callSuper = true)
@Data
public class Player extends LiveObject {

    private final Session session;
    private final PlayerCurrencies playerCurrencies;
    private final PlayerStats stats;
    private final PlayerTask playerTask;
    private final PlayerFashion playerFashion;
    private final PlayerSkill playerSkill;
    private final PlayerInventory playerInventory;

    private Area area;

    public Player(Session session) {
        this.session = session;
        this.playerCurrencies = new PlayerCurrencies(this);
        this.stats = new PlayerStats(this);
        this.playerTask = new PlayerTask(this);
        this.playerFashion = new PlayerFashion(this);
        this.playerSkill = new PlayerSkill(this);
        this.playerInventory = new PlayerInventory(this);
    }

    public void sendMessage(Message message) {
        this.session.sendMessage(message);
    }

}
