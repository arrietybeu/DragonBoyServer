package nro.model.player;

import lombok.Data;

@Data
public class PlayerCurrencies {

    private final Player player;

    private long gold;
    private int gem;
    private int ruby;

    public PlayerCurrencies(Player player) {
        this.player = player;
    }

    public void addGold(long num) {
        this.gold += num;
        if (this.gold >= 10_000_000_000L) {
            this.gold = Long.MAX_VALUE;
        }
    }

    public void subGold(long num) {
        this.gold -= num;
        if (this.gold < 0L) {
            this.gold = 0L;
        }
    }

    public void addGem(int num) {
        this.gem += num;
        if (this.gem >= 1000000000) {
            this.gem = 1000000000;
        }
    }

    public void subGem(int num) {
        this.gem -= num;
        if (this.gem < 0) {
            this.gem = 0;
        }
    }

    public void addRuby(int num) {
        this.ruby += num;
        if (this.ruby >= 1000000000) {
            this.ruby = 1000000000;
        }
    }

    public void subRuby(int num) {
        this.ruby -= num;
        if (this.ruby < 0) {
            this.ruby = 0;
        }
    }

    public Player getPlayer() {
        if (player == null) {
            throw new NullPointerException("Player is null");
        }
        return player;
    }

}
