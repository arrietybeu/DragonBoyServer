package nro.server.service.core.economy;

import lombok.Getter;
import lombok.Setter;
import nro.server.service.core.system.ServerService;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.item.Item;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TradeSession {

    private static final int MAX_GOLD = 900_000_000;
    private final int idTrade;

    @Setter
    private long createTime;

    private int goldPlayer1;
    private int goldPlayer2;

    private boolean lockPlayer1 = false;
    private boolean lockPlayer2 = false;

    private boolean donePlayer1 = false;
    private boolean donePlayer2 = false;

    private Player player1;
    private Player player2;
    private final List<Item> offerPlayer1 = new ArrayList<>();
    private final List<Item> offerPlayer2 = new ArrayList<>();

    public TradeSession(int id, Player p1, Player p2) {
        this.idTrade = id;
        this.player1 = p1;
        this.player2 = p2;
        this.setIdTradeForPlayer(id);
    }

    private void setIdTradeForPlayer(int id) {
        this.player1.getPlayerContext().setIdTrade(id);
        this.player2.getPlayerContext().setIdTrade(id);
    }

    public boolean addItem(Player player, Item item) {
        List<Item> offerList = this.getOfferList(player);
        if (offerList == null || isPlayerLockedOrDone(player)) {
            return false;
        }
        return this.addToList(offerList, item);
    }

    private List<Item> getOfferList(Player player) {
        if (player.equals(player1)) {
            return offerPlayer1;
        } else if (player.equals(player2)) {
            return offerPlayer2;
        }
        return null;
    }

    private boolean isPlayerLockedOrDone(Player player) {
        if (player.equals(player1)) {
            return this.isLockPlayer1() || this.isDonePlayer1();
        } else if (player.equals(player2)) {
            return this.isLockPlayer2() || this.isDonePlayer2();
        }
        return true;
    }

    private boolean addToList(List<Item> list, Item item) {

        if (list.contains(item)) {
            ServerService.getInstance().sendChatGlobal(player1.getSession(), null, "Bạn đã có món đồ này trong giao dịch", false);
            return false;
        }

        if (list.size() > 10) {
            ServerService.getInstance().sendChatGlobal(player1.getSession(), null, "Bạn chỉ có thể giao dịch tối đa 10 món đồ", false);
            return false;
        }

        if (item.getQuantity() < 0) {
            return false;
        }

        list.add(item);
        return true;
    }

    public void addGold(Player player, int gold) {
        if (gold < 0 || gold > MAX_GOLD) {
            return;
        }
        if (player.getPlayerCurrencies().getGold() < gold) {
            ServerService.getInstance().sendChatGlobal(player.getSession(), null, "Không đủ vàng để giao dịch", false);
            return;
        }
        if (player.equals(player1)) {
            goldPlayer1 = gold;
        } else {
            goldPlayer2 = gold;
        }
    }

    public Player getOpponent(Player player) {
        return player.equals(player1) ? player2 : player1;
    }

    public void done(Player player) {
        if (player.equals(player1)) {
            donePlayer1 = true;
        } else {
            donePlayer2 = true;
        }
    }

    public void lock(Player player) {
        if (player.equals(player1)) {
            lockPlayer1 = true;
        } else {
            lockPlayer2 = true;
        }
    }

    public boolean isBothDone() {
        return donePlayer1 && donePlayer2;
    }

    public boolean isBothLocked() {
        return lockPlayer1 && lockPlayer2;
    }

    public void reset() {
        // clear list of items
        offerPlayer1.clear();
        offerPlayer2.clear();

        donePlayer1 = false;
        donePlayer2 = false;

        if (player1 != null) {
            player1.getPlayerContext().setIdTrade(-1);
            player1 = null;
        }

        if (player2 != null) {
            player2.getPlayerContext().setIdTrade(-1);
            player2 = null;
        }

        createTime = 0;
    }
}
