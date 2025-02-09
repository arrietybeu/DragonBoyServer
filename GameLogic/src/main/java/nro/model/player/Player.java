package nro.model.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nro.model.LiveObject;
import nro.model.clan.Clan;
import nro.model.map.areas.Area;
import nro.model.pet.Disciple;
import nro.network.Message;
import nro.network.Session;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
    private final PlayerFusion playerFusion;
    private final Instant createdAt;

    private Area area;
    private Clan clan;
    private Disciple disciple;

    private int role;
    private int activePoint;
    private int rank;

    public Player(Session session, Instant createdAt) {
        this.session = session;
        this.createdAt = createdAt;
        this.playerCurrencies = new PlayerCurrencies(this);
        this.stats = new PlayerStats(this);
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
        System.out.println("Người chơi " + this.getName() +" đã tạo tài khoản được " + days + " ngày.");
        return days < 30;
    }

    public long getDaysSinceCreation() {
        return ChronoUnit.DAYS.between(createdAt, Instant.now());
    }



}
