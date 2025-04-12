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
        this.player1.getPlayerState().setIdTrade(id);
        this.player2.getPlayerState().setIdTrade(id);
    }

    public Player getOpponent(Player player) {
        return player.equals(player1) ? player2 : player1;
    }

    public void addItem(Player player, Item item) {
        if (player.equals(player1)) {
            if (this.checkValidSize(offerPlayer1.size())) return;
            offerPlayer1.add(item);
        } else {
            if (this.checkValidSize(offerPlayer2.size())) return;
            offerPlayer2.add(item);
        }
    }

    private boolean checkValidSize(int size) {
        if (size > 10) {
            ServerService.getInstance().sendChatGlobal(player1.getSession(), null, "Bạn chỉ có thể giao dịch tối đa 10 món đồ", false);
            return true;
        }
        return false;
    }

    public void addGold(Player player, int gold) {
        if (gold < 0) {
            return;
        }
        if (player.getPlayerCurrencies().getGold() < gold) {
            ServerService.getInstance().sendChatGlobal(player.getSession(), null, "Không đủ vàng để giao dịch", false);
            return;
        }

        if (gold > MAX_GOLD) {
            return;
        }

        if (player.equals(player1)) {
            goldPlayer1 = gold;
        } else {
            goldPlayer2 = gold;
        }
    }

    public void done(Player player) {
        if (player.equals(player1)) {
            donePlayer1 = true;
        } else {
            donePlayer2 = true;
        }
    }

    public boolean isBothDone() {
        return donePlayer1 && donePlayer2;
    }

    public void reset() {
        // clear list of items
        offerPlayer1.clear();
        offerPlayer2.clear();

        donePlayer1 = false;
        donePlayer2 = false;

        if (player1 != null) {
            player1.getPlayerState().setIdTrade(-1);
            player1 = null;
        }

        if (player2 != null) {
            player2.getPlayerState().setIdTrade(-1);
            player2 = null;
        }

        createTime = 0;
    }
}
