package nro.model.player;

import lombok.Getter;
import lombok.Setter;
import nro.service.PlayerService;
import nro.service.Service;

@Getter
@Setter
public class PlayerCurrencies {

    private final Player player;

    private int gem;
    private int ruby;
    private long gold;

    public PlayerCurrencies(Player player) {
        this.player = player;
    }

    public void addGold(long num) {
        this.gold += num;
        if (this.gold >= 100_000_000_000L) {
            this.gold = Long.MAX_VALUE;
        }
        PlayerService.getInstance().sendCurrencyHpMp(player);
    }

    public void subGold(long num) {
        this.gold -= num;
        if (this.gold < 0L) {
            this.gold = 0L;
        }
        PlayerService.getInstance().sendCurrencyHpMp(player);
    }

    public void addGem(int num) {
        this.gem += num;
        if (this.gem >= 1000000000) {
            this.gem = 1000000000;
        }
        PlayerService.getInstance().sendCurrencyHpMp(player);
    }

    public void addRuby(int num) {
        this.ruby += num;
        if (this.ruby >= 1000000000) {
            this.ruby = 1000000000;
        }
        PlayerService.getInstance().sendCurrencyHpMp(player);
    }

    private int getTotalCurrency() {
        return this.ruby + this.gem;
    }

    public boolean subCurrency(int amount) {
        System.out.println("subCurrency: " + amount);
        PlayerService playerService = PlayerService.getInstance();
        if (getTotalCurrency() < amount) {
            String info = String.format("Bạn không đủ ngọc, còn thiếu %d ngọc nữa", amount - getTotalCurrency());
            Service.getInstance().sendChatGlobal(this.player.getSession(), null, info, false);
            return false;
        }

        if (this.ruby >= amount) {
            subRuby(amount);
        } else {
            int remaining = amount - this.ruby;
            subRuby(this.ruby);
            subGem(remaining);
        }
        playerService.sendCurrencyHpMp(player);
        return true;
    }

    private void subRuby(int num) {
        this.ruby -= num;
        if (this.ruby < 0) {
            this.ruby = 0;
        }
    }

    private void subGem(int num) {
        this.gem -= num;
        if (this.gem < 0) {
            this.gem = 0;
        }
        PlayerService.getInstance().sendCurrencyHpMp(player);
    }

    @Override
    public String toString() {
        return "PlayerCurrencies{" + "gold=" + gold + ", gem=" + gem + ", ruby=" + ruby + '}';
    }

}
