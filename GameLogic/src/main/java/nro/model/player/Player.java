package nro.model.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nro.model.LiveObject;
import nro.model.clan.Clan;
import nro.model.map.areas.Area;
import nro.model.discpile.Disciple;
import nro.network.Message;
import nro.network.Session;
import nro.service.AreaService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@EqualsAndHashCode(callSuper = true)
@Data
public class Player extends LiveObject {

    private final Session session;
    private final PlayerCurrencies playerCurrencies;
    private final PlayerStats playerStats;
    private final PlayerTask playerTask;
    private final PlayerFashion playerFashion;
    private final PlayerSkill playerSkill;
    private final PlayerInventory playerInventory;
    private final PlayerFusion playerFusion;
    private final Instant createdAt;

    private Area area;
    private Clan clan;
    private Disciple disciple;

    private int role;
    private int activePoint;
    private int rank;

    // -1 (có lia cam) || 0 (không lia cam) || 1 có lia cam ||
    private int teleport = 0;

    public Player(Session session, Instant createdAt) {
        this.session = session;
        this.createdAt = createdAt;
        this.playerCurrencies = new PlayerCurrencies(this);
        this.playerStats = new PlayerStats(this);
        this.playerTask = new PlayerTask(this);
        this.playerFashion = new PlayerFashion(this);
        this.playerSkill = new PlayerSkill(this);
        this.playerInventory = new PlayerInventory(this);
        this.playerFusion = new PlayerFusion(this);
    }

    public void sendMessage(Message message) {
        this.session.sendMessage(message);
    }

    public boolean isNewPlayer() {
        long days = this.getDaysSinceCreation();
//        System.out.println("Player name: " + this.getName() + " create " + days + " day.");
        return days < 30;
    }

    private long getDaysSinceCreation() {
        return ChronoUnit.DAYS.between(createdAt, Instant.now());
    }

    @Override
    public void update() {
    }

    @Override
    public void dispose() {
        AreaService.getInstance().playerExitArea(this);
    }

}
