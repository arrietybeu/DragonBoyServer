package nro.model.player;

import lombok.Getter;
import nro.model.LiveObject;
import nro.network.Message;
import nro.network.Session;

public class Player extends LiveObject {

    @Getter
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
