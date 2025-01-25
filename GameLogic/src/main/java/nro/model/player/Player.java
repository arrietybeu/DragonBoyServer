package nro.model.player;

import lombok.Data;
import nro.model.LiveObject;
import nro.network.Message;
import nro.network.Session;

@Data
public class Player extends LiveObject {

    private final Currencies currencies;
    private final PlayerStats stats;

    private final Session session;

    public Player(Session session) {
        this.session = session;
        this.currencies = new Currencies(this);
        this.stats = new PlayerStats(this);
    }

    public void sendMessage(Message message) {
        this.session.sendMessage(message);
    }

}
