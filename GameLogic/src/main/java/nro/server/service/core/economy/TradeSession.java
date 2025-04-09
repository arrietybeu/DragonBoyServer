package nro.server.service.core.economy;

import lombok.Getter;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.item.Item;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TradeSession {

    private final Player player1;
    private final Player player2;
    private final List<Item> offerPlayer1 = new ArrayList<>();
    private final List<Item> offerPlayer2 = new ArrayList<>();
    private boolean lockedPlayer1 = false;
    private boolean lockedPlayer2 = false;

    public TradeSession(Player p1, Player p2) {
        this.player1 = p1;
        this.player2 = p2;
    }

    public Player getOpponent(Player player) {
        return player.equals(player1) ? player2 : player1;
    }

    public void addItem(Player player, Item item) {
        if (player.equals(player1)) {
            offerPlayer1.add(item);
        } else {
            offerPlayer2.add(item);
        }
    }

    public void lock(Player player) {
        if (player.equals(player1)) {
            lockedPlayer1 = true;
        } else {
            lockedPlayer2 = true;
        }
    }

    public boolean isBothLocked() {
        return lockedPlayer1 && lockedPlayer2;
    }

    public void reset() {
        offerPlayer1.clear();
        offerPlayer2.clear();
        lockedPlayer1 = false;
        lockedPlayer2 = false;
    }
}
